package com.office_management.auth_service.config.otp;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OtpStore {

    private final Map<String, OtpRecord> otpStore = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastSent = new ConcurrentHashMap<>();

    public void save(String key, String otp) {
        LocalDateTime now = LocalDateTime.now();
        otpStore.put(key, new OtpRecord(otp, now.plusMinutes(OtpConstants.OTP_EXPIRATION_MINUTES), now, 0));
        lastSent.put(key, now);
    }

    public boolean canSend(String key) {
        LocalDateTime lastSentTime = lastSent.get(key);
        if (lastSentTime == null) {
            return true;
        }

        return lastSentTime.plusSeconds(OtpConstants.RESEND_COOLDOWN_SECONDS)
                .isBefore(LocalDateTime.now());
    }

    public long getRemainingCooldownSeconds(String key) {
        LocalDateTime lastSentTime = lastSent.get(key);
        if (lastSentTime == null) {
            return 0;
        }

        long remaining = Duration.between(LocalDateTime.now(),
                lastSentTime.plusSeconds(OtpConstants.RESEND_COOLDOWN_SECONDS)).getSeconds();
        return Math.max(remaining, 0);
    }

    public boolean validate(String key, String submittedOtp) {
        OtpRecord record = otpStore.get(key);
        if (record == null) {
            return false;
        }

        if (record.isExpired()) {
            otpStore.remove(key);
            return false;
        }

        if (!record.canAttempt()) {
            otpStore.remove(key);
            return false;
        }

        record.incrementAttempts();

        if (record.getOtp().equals(submittedOtp)) {
            otpStore.remove(key);
            lastSent.remove(key);
            return true;
        }
        return false;
    }

    public void remove(String key) {
        otpStore.remove(key);
    }
}