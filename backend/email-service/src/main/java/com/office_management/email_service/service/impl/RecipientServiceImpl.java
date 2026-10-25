package com.office_management.email_service.service.impl;

import com.office_management.email_service.dto.UserEmailDTO;
import com.office_management.email_service.exceptions.errors.FeignServiceException;
import com.office_management.email_service.exceptions.errors.UserNotFoundException;
import com.office_management.email_service.feign.AuthUserFeignClient;
import com.office_management.email_service.service.RecipientService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipientServiceImpl implements RecipientService {
    private final AuthUserFeignClient authUserFeignClient;

    @Override
    public UserEmailDTO getUserById(Long userId) {
        try {
            UserEmailDTO user = authUserFeignClient.getUserById(userId);

            if (user == null) {
                throw new UserNotFoundException("User with ID " + userId + " not found");
            }

            return user;
        } catch (FeignException.NotFound exception) {
            throw new UserNotFoundException("User with ID "+ userId + " not found");
        } catch (FeignException exception) {
            log.error("Failed to retrieve user {} from Auth Service. " +"HTTP status: {}", userId, exception.status(), exception);
            throw new FeignServiceException("Auth Service is unavailable", exception);
        }
    }

    @Override
    public String getEmailAddress(Long userId) {
        UserEmailDTO user = getUserById(userId);

        if (Boolean.FALSE.equals(user.getEnabled())) {
            log.warn("User {} is disabled. " + "Email notification will not be sent.", userId);
            return null;
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("User {} does not have a valid email address.", userId);
            return null;
        }
        return user.getEmail();
    }
}