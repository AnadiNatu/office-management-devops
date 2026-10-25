package com.office_management.auth_service.service;


import com.office_management.auth_service.dto.ResetPasswordRequest;
import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.exception.errors.UserNotFoundException;
import com.office_management.auth_service.repository.UserRepository;
import com.office_management.auth_service.service.interfaces.NotificationService;
import com.office_management.auth_service.service.interfaces.OtpService;
import com.office_management.auth_service.service.interfaces.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    public void requestReset(String email) {
        Users user = userRepository.findByEmailIgnoreCase(email.trim())
                        .orElseThrow(() -> new UserNotFoundException("No account found with this email"));

        String otp = otpService.generateAndStore("PASSWORD_RESET:" + user.getEmail().toLowerCase());

        notificationService.sendPasswordResetOtp(user.getEmail(), user.getFname(), otp);
    }

    public void resetPassword(ResetPasswordRequest request) {
        Users user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
                        .orElseThrow(() -> new UserNotFoundException("No account found with this email"));

        otpService.verify("PASSWORD_RESET:" + user.getEmail().toLowerCase(), request.getOtp());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

}