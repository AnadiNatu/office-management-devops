package com.office_management.auth_service.service.interfaces;

public interface OtpService {

    public String generateAndStore(String key);

    public void verify(String key, String submittedOtp);

    public boolean isValid(String key, String otp);
}