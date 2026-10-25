package com.office_management.auth_service.config.otp;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OtpRecord {
    private final String otp;

    private final LocalDateTime expiresAt;

    private final LocalDateTime createdAt;

    private int attempts;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean canAttempt() {
        return attempts < OtpConstants.MAX_ATTEMPTS;
    }

    public void incrementAttempts() {
        attempts++;
    }
}