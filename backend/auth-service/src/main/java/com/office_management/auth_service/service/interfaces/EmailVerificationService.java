package com.office_management.auth_service.service.interfaces;

public interface EmailVerificationService {
    public void sendVerificationOtp(String email);
    public void verifyEmail(String email, String otp);
}
