package com.office_management.message_service.service.impl;

import com.office_management.message_service.dto.feign.UserSummaryDTO;
import com.office_management.message_service.exception.errors.AuthServiceUnavailableException;
import com.office_management.message_service.exception.errors.UserDisabledException;
import com.office_management.message_service.exception.errors.UserNotFoundException;
import com.office_management.message_service.feign.AuthUserFeignClient;
import com.office_management.message_service.service.AuthUserService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserFeignClient authUserFeignClient;

    @Override
    public UserSummaryDTO getUserById(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        try {
            UserSummaryDTO user = authUserFeignClient.getUserById(userId);
            if (user == null) {
                throw new UserNotFoundException("User not found with ID: " + userId);
            }
            return user;
        } catch (FeignException.NotFound ex) {
            log.warn("User not found in Auth Service: {}", userId);
            throw new UserNotFoundException("User not found with ID: " + userId);
        } catch (FeignException ex) {
            log.error("Auth Service request failed while fetching user {}", userId, ex);
            throw new AuthServiceUnavailableException("Unable to communicate with Auth Service", ex);
        }
    }

    @Override
    public UserSummaryDTO validateUser(Long userId) {

        UserSummaryDTO user = getUserById(userId);
        if (!user.isEnabled()) {
            log.warn("Attempt to use disabled user: {}", userId);

            throw new UserDisabledException("User account is disabled");
        }

        return user;
    }

    @Override
    public boolean userExists(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        try {
            Boolean exists = authUserFeignClient.userExists(userId);
            return Boolean.TRUE.equals(exists);
        } catch (FeignException ex) {
            log.error("Auth Service request failed while checking user {}", userId, ex);
            throw new AuthServiceUnavailableException("Unable to communicate with Auth Service", ex);
        }
    }
}