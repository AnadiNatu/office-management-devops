package com.office_management.auth_service.service.interfaces;

public interface OtpLoginService {
    public void sendEmailOtp(String email);
    public void verifyEmailOtp(String email, String otp);
}
