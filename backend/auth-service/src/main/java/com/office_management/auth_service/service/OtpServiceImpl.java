package com.office_management.auth_service.service;

import com.office_management.auth_service.config.otp.OtpGenerator;
import com.office_management.auth_service.config.otp.OtpStore;
import com.office_management.auth_service.exception.errors.InvalidTokenException;
import com.office_management.auth_service.exception.errors.ValidationException;
import com.office_management.auth_service.service.interfaces.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final OtpGenerator otpGenerator;
    private final OtpStore otpStore;

    public String generateAndStore(String key) {

        if (key == null || key.isBlank()) {
            throw new ValidationException("OTP identifier cannot be empty");
        }

        String normalizedKey = normalize(key);
        if (!otpStore.canSend(normalizedKey)) {
            long remaining = otpStore.getRemainingCooldownSeconds(normalizedKey);

            throw new ValidationException("Please wait " + remaining + " seconds before requesting another OTP");
        }

        String otp = otpGenerator.generate();

        otpStore.save(normalizedKey, otp);

        return otp;
    }

    public void verify(String key, String submittedOtp) {

        if (key == null || key.isBlank()) {
            throw new ValidationException("OTP identifier is required");
        }

        if (submittedOtp == null || submittedOtp.isBlank()) {
            throw new ValidationException("OTP is required");
        }
        String normalizedKey = normalize(key);
        boolean valid = otpStore.validate(normalizedKey, submittedOtp.trim());

        if (!valid) {
            throw new InvalidTokenException("Invalid or expired OTP");
        }
    }

    public boolean isValid(String key, String otp) {
        try {
            verify(key, otp);
            return true;
        } catch (InvalidTokenException ex) {
            return false;
        }
    }

    private String normalize(String value) {
        return value.trim().toLowerCase();
    }
}