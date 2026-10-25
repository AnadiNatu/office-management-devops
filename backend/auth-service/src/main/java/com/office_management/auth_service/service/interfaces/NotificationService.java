package com.office_management.auth_service.service.interfaces;

public interface NotificationService {

    public void sendOtp(String email,String firstName, String otp, String eventType);
    public void sendWelcomeEmail(String email, String firstName);
    public void sendLoginNotification(String email, String firstName);
    public void sendPasswordResetOtp(String email, String firstName, String otp);
}