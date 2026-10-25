package com.office_management.auth_service.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TwilioConfig {
    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String phoneNumber;

    @PostConstruct
    public void initTwilio() {

        if (accountSid == null || accountSid.isBlank()) {
            throw new IllegalStateException("Twilio Account SID is not configured");
        }

        if (authToken == null || authToken.isBlank()) {
            throw new IllegalStateException("Twilio Auth Token is not configured");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalStateException("Twilio phone number is not configured");
        }

        Twilio.init(accountSid, authToken);
    }
}