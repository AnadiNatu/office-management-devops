package com.office_management.auth_service.service;

import com.office_management.auth_service.dto.feign.EmailNotificationRequest;
import com.office_management.auth_service.exception.errors.FeignServiceException;
import com.office_management.auth_service.feign.NotificationFeignClient;
import com.office_management.auth_service.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationFeignClient notificationFeignClient;

    public void sendOtp(String email, String firstName, String otp, String eventType) {
        try {
            notificationFeignClient.sendOtpEmail(
                    EmailNotificationRequest.builder()
                            .email(email)
                            .firstName(firstName)
                            .otp(otp)
                            .eventType(eventType)
                            .build());

        } catch (Exception ex) {
            log.error("Failed to send OTP email | email={}", email, ex);

            throw new FeignServiceException("Notification service is unavailable", ex);
        }
    }

    public void sendWelcomeEmail(String email, String firstName) {
        try {

            notificationFeignClient.sendWelcomeEmail(
                    EmailNotificationRequest.builder()
                            .email(email)
                            .firstName(firstName)
                            .eventType("WELCOME")
                            .build());

        } catch (Exception ex) {
            log.error("Failed to send welcome email | email={}", email, ex);
            throw new FeignServiceException("Failed to send welcome email", ex);
        }
    }

    public void sendLoginNotification(String email, String firstName) {
        try {
            notificationFeignClient.sendLoginNotification(
                    EmailNotificationRequest.builder()
                            .email(email)
                            .firstName(firstName)
                            .eventType("LOGIN")
                            .build());

        } catch (Exception ex) {
            log.warn("Login notification failed | email={}", email, ex);
        }
    }

    public void sendPasswordResetOtp(String email, String firstName, String otp) {
        sendOtp(email, firstName, otp, "PASSWORD_RESET");
    }
}