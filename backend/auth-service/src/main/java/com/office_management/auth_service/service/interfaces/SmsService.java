package com.office_management.auth_service.service.interfaces;

public interface SmsService {
    public void sendSms(String toPhone, String body);
}
