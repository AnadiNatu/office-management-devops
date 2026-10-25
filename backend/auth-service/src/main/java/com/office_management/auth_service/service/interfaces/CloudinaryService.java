package com.office_management.auth_service.service.interfaces;

import com.office_management.auth_service.enums.UserRoles;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadProfilePhoto(MultipartFile file, Long userId, UserRoles role);

    void deleteProfilePhoto(Long userId, UserRoles role);
}