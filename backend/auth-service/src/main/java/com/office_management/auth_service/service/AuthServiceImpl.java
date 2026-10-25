package com.office_management.auth_service.service;

import com.office_management.auth_service.config.rabbit.AuthEventPublisher;
import com.office_management.auth_service.dto.*;
import com.office_management.auth_service.dto.kafka.UserCreatedMessage;
import com.office_management.auth_service.dto.kafka.UserProfileUpdatedMessage;
import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.enums.UserRoles;
import com.office_management.auth_service.exception.errors.*;
import com.office_management.auth_service.mapper.UserMapper;
import com.office_management.auth_service.repository.UserRepository;
import com.office_management.auth_service.security.JwtUtil;
import com.office_management.auth_service.security.UserDetailServiceImpl;
import com.office_management.auth_service.service.interfaces.AuthService;
import com.office_management.auth_service.service.interfaces.CloudinaryService;
import com.office_management.auth_service.service.rabbit.AuthenticationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final UserMapper userMapper;

    private final AuthEventPublisher authEventPublisher;

    private final CloudinaryService cloudinaryService;

    private final AuthenticationPublisher authNotificationPublisher;

    @Override
    public UserResponseDTO signup(SignUpRequest request) {

        StopWatch stopWatch = new StopWatch("signup");
        stopWatch.start();

        try {
            validateSignupRequest(request);

            String email = request.getEmail().trim().toLowerCase();

            if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new DuplicateResourceException("Email is already registered");
            }

            if (request.getPhoneNumber() != null
                    && !request.getPhoneNumber().isBlank()
                    && userRepository.existsByPhoneNumber(request.getPhoneNumber().trim()))
            {
                throw new DuplicateResourceException("Phone number is already registered");
            }

            Users user = Users.builder().fname(request.getFname().trim())
                            .lname(request.getLname().trim())
                            .email(email)
                            .password(passwordEncoder.encode(request.getPassword()))
                            .phoneNumber(normalizePhone(request.getPhoneNumber()))
                            .role(UserRoles.EMPLOYEE)
                            .enabled(true)
                            .emailVerified(false)
                            .build();

            Users savedUser = userRepository.save(user);

            UserCreatedMessage message = userMapper.toUserCreatedMessage(savedUser);
            authEventPublisher.publishUserCreated(message);

            log.info("[AUTH] Signup successful | userId={} | email={} | role={} | duration={}ms", savedUser.getId(), savedUser.getEmail(), savedUser.getRole(), stopWatch.getTotalTimeMillis());

            return userMapper.toResponseDTO(savedUser);
        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        StopWatch stopWatch = new StopWatch("login");

        stopWatch.start();

        if (request == null
                || request.getUsername() == null
                || request.getUsername().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {

            throw new InvalidCredentialsException("Email and password are required");
        }
        String username = request.getUsername().trim().toLowerCase();

        log.info("[AUTH] Login attempt | username={}", username);

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));
            Users user = (Users) authentication.getPrincipal();

            if (!Boolean.TRUE.equals(user.getEnabled())) {
                throw new AccountDisabledException("User account is disabled");
            }

            if (!Boolean.TRUE.equals(user.getEmailVerified())) {
                throw new EmailNotVerifiedException("Please verify your email before logging in");
            }
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            stopWatch.stop();

            log.info("[AUTH] Login successful | userId={} | role={} | duration={}ms", user.getId(), user.getRole(), stopWatch.getTotalTimeMillis());

            authNotificationPublisher.publishLoginEvent(user.getId(), user.getEmail(), user.getFname(), user.getPhoneNumber());

            return LoginResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .username(user.getEmail())
                    .email(user.getEmail())
                    .fname(user.getFname())
                    .lname(user.getLname())
                    .role(user.getRole())
                    .profilePicture(user.getProfilePicture())
                    .message("Login successful")
                    .build();

        } catch (BadCredentialsException ex) {
            log.warn("[AUTH] Invalid credentials | username={}", username);
            throw new InvalidCredentialsException("Invalid email or password");

        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUser(Long userId) {
        return userMapper.toResponseDTO(findUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long userId) {
        return userMapper.toResponseDTO(findUser(userId));
    }

    @Override
    public UserResponseDTO provisionUser(ProvisionUserRequest request, Long provisionedByUserId) {
        if (request == null) {
            throw new ValidationException("Provision request cannot be null");
        }

        Users provisioner = findUser(provisionedByUserId);
        validateProvisioningAuthority(provisioner, convertToUserRole(request.getRole()));
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {

            throw new DuplicateResourceException(
                    "Email is already registered"
            );
        }

        Users user = Users.builder().fname(request.getFname().trim())
                        .lname(request.getLname().trim())
                        .email(email)
                        .password(passwordEncoder.encode(request.getPassword()))
                        .phoneNumber(normalizePhone(request.getPhoneNumber()))
                        .role(convertToUserRole(request.getRole()))
                        .enabled(true)
                        .emailVerified(false)
                        .build();
        Users saved = userRepository.save(user);
        authEventPublisher.publishUserCreated(userMapper.toUserCreatedMessage(saved));

        log.info("[AUTH] User provisioned | userId={} | role={} | provisionedBy={}", saved.getId(), saved.getRole(), provisioner.getId());
        return userMapper.toResponseDTO(saved);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Refresh token is required");
        }

        try {

            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw new InvalidTokenException("Invalid refresh token");
            }

            String email = jwtUtil.extractUsername(refreshToken);
            Long userId = jwtUtil.extractUserId(refreshToken);
            Users user = findUser(userId);

            if (!user.getEmail().equalsIgnoreCase(email)) {
                throw new InvalidTokenException("Refresh token does not belong to the user");
            }

            if (!Boolean.TRUE.equals(user.getEnabled())) {
                throw new AccountDisabledException("User account is disabled");
            }
            String accessToken = jwtUtil.generateAccessToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);

            return LoginResponse.builder()
                    .token(accessToken)
                    .refreshToken(newRefreshToken)
                    .userId(user.getId())
                    .username(user.getEmail())
                    .email(user.getEmail())
                    .fname(user.getFname())
                    .lname(user.getLname())
                    .role(user.getRole())
                    .profilePicture(user.getProfilePicture())
                    .message("Token refreshed successfully")
                    .build();

        } catch (InvalidTokenException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("[AUTH] Refresh token processing failed | error={}", ex.getMessage(), ex);
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {

        if (request == null) {
            throw new ValidationException("Password change request cannot be null");
        }
        Users user = findUser(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ValidationException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("[AUTH] Password changed | userId={}", userId);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        if (request == null) {
            throw new ValidationException("Password reset request cannot be null");
        }

        Users user = userRepository.findByEmailIgnoreCase(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("[AUTH] Password reset | userId={}", user.getId());
    }

    @Override
    public void uploadProfilePhoto(
            Long userId, MultipartFile file) {

        Users user = findUser(userId);

        String photoUrl = cloudinaryService.uploadProfilePhoto(file, user.getId(), user.getRole());
        user.setProfilePicture(photoUrl);
        Users saved = userRepository.save(user);
        UserProfileUpdatedMessage message = userMapper.toProfileUpdatedMessage(saved);
        authEventPublisher.publishUserProfileUpdated(message);

        log.info("[AUTH] Profile photo updated | userId={} | role={}", userId, user.getRole());
    }

    @Override
    public void removeProfilePhoto(Long userId) {

        Users user = findUser(userId);
        if (user.getProfilePicture() == null || user.getProfilePicture().isBlank()) {
            return;
        }

        cloudinaryService.deleteProfilePhoto(user.getId(), user.getRole());
        user.setProfilePicture(null);
        Users saved = userRepository.save(user);
        authEventPublisher.publishUserProfileUpdated(userMapper.toProfileUpdatedMessage(saved));
        log.info("[AUTH] Profile photo removed | userId={}", userId);
    }

    @Override
    public UserResponseDTO disableUser(Long userId) {
        Users user = findUser(userId);
        user.setEnabled(false);
        Users savedUser = userRepository.save(user);
        log.info("[AUTH] User disabled | userId={}", userId);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO enableUser(Long userId) {
        Users user = findUser(userId);
        user.setEnabled(true);
        Users savedUsers = userRepository.save(user);
        log.info("[AUTH] User enabled | userId={}", userId);
        return userMapper.toResponseDTO(savedUsers);
    }

    @Override
    public UserResponseDTO changeUserRole(Long userId , UserRoles roles){
        Users user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found"));

        if (roles == null) {
            throw new ValidationException("Role is required");
        }

        user.setRole(roles);
        userRepository.save(user);
        authEventPublisher.publishUserProfileUpdated(userMapper.toProfileUpdatedMessage(user));

        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {

        log.info("[AUTH] Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(UserRoles role) {

        if (role == null) {
            throw new IllegalArgumentException("User role is required");
        }

        log.info("[AUTH] Fetching users by role | role={}", role);

        return userRepository.findByRole(role)
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

// ====== HELPERS ======
    private Users findUser(Long userId) {
        if (userId == null) {
            throw new UserNotFoundException("User ID cannot be null");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    private void validateSignupRequest(SignUpRequest request) {

        if (request == null) {
            throw new ValidationException("Signup request cannot be null");
        }

        if (isBlank(request.getFname())) {
            throw new ValidationException("First name is required");
        }

        if (isBlank(request.getLname())) {
            throw new ValidationException("Last name is required");
        }

        if (isBlank(request.getEmail())) {
            throw new ValidationException("Email is required");
        }

        if (isBlank(request.getPassword())) {
            throw new ValidationException("Password is required");
        }
    }

    private void validateProvisioningAuthority(Users provisioner, UserRoles requestedRole) {

        if (provisioner == null || provisioner.getRole() == null) {
            throw new UnauthorizedActionException("Unable to determine provisioning authority");
        }

        if (requestedRole == null) {
            throw new ValidationException("User role is required");
        }

        UserRoles provisionerRole =
                provisioner.getRole();

        if (provisionerRole == UserRoles.ADMIN) {
            return;
        }

        if (provisionerRole == UserRoles.MANAGER && requestedRole == UserRoles.EMPLOYEE) {
            return;
        }
        throw new UnauthorizedActionException("You are not authorized to provision this user role");
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return phone.trim();
    }

    private UserRoles convertToUserRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new ValidationException("Role is required");
        }

        try {
            return UserRoles.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException(
                    "Invalid role: " + role +
                            ". Allowed roles are: ADMIN, MANAGER, EMPLOYEE"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private LoginResponse buildLoginResponse(Users user) {

        String accessToken =
                jwtUtil.generateAccessToken(user);

        String refreshToken =
                jwtUtil.generateRefreshToken(user);

        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getEmail())
                .email(user.getEmail())
                .fname(user.getFname())
                .lname(user.getLname())
                .role(user.getRole())
                .profilePicture(user.getProfilePicture())
                .message("Login successful")
                .build();
    }
}