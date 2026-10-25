package com.office_management.auth_service.feign;

import com.office_management.auth_service.config.FeignConfig;
import com.office_management.auth_service.dto.feign.EmailNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", contextId = "notificationFeignClient" ,configuration = FeignConfig.class)
public interface NotificationFeignClient {
    @PostMapping("/api/notifications/email/otp")
    void sendOtpEmail(@RequestBody EmailNotificationRequest request);

    @PostMapping("/api/notifications/email/welcome")
    void sendWelcomeEmail(@RequestBody EmailNotificationRequest request);

    @PostMapping("/api/notifications/email/login")
    void sendLoginNotification(@RequestBody EmailNotificationRequest request);

    @PostMapping("/api/notifications/email/password-reset")
    void sendPasswordResetEmail(@RequestBody EmailNotificationRequest request);
}