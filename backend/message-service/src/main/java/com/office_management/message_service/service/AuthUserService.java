package com.office_management.message_service.service;

import com.office_management.message_service.dto.feign.UserSummaryDTO;

public interface AuthUserService {
    UserSummaryDTO getUserById(Long userId);
    UserSummaryDTO validateUser(Long userId);
    boolean userExists(Long userId);
}