package com.office_management.auth_service.config.otp;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private final SecureRandom secureRandom = new SecureRandom();
    public String generate() {
        int minimum = 100000;
        int maximum = 999999;
        return String.valueOf(secureRandom.nextInt(maximum - minimum + 1) + minimum);
    }
}