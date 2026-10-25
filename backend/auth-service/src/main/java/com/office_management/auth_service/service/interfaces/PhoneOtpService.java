package com.office_management.auth_service.service.interfaces;

public interface PhoneOtpService {
    public void sendOtp(String phoneNumber);
    public void verifyOtp(String phoneNumber, String otp);
}