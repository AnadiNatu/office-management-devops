package com.office_management.auth_service.service.interfaces;

import com.office_management.auth_service.dto.*;
import com.office_management.auth_service.enums.UserRoles;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuthService {

    UserResponseDTO signup(SignUpRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(String refreshToken);

    UserResponseDTO provisionUser(ProvisionUserRequest request, Long provisionedByUserId);

    UserResponseDTO getCurrentUser(Long userId);

    UserResponseDTO getUserById(Long userId);

    void changePassword(Long userId, ChangePasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void uploadProfilePhoto(Long userId, MultipartFile file);

    void removeProfilePhoto(Long userId);

    UserResponseDTO disableUser(Long userId);

    UserResponseDTO enableUser(Long userId);

    UserResponseDTO changeUserRole(Long userId , UserRoles userRoles);

    List<UserResponseDTO> getAllUsers();

    List<UserResponseDTO> getUsersByRole(UserRoles role);
}