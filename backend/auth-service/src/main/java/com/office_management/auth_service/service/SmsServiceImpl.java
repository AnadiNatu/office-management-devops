package com.office_management.auth_service.service;

import com.office_management.auth_service.config.TwilioConfig;
import com.office_management.auth_service.service.interfaces.SmsService;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    private final TwilioConfig twilioConfig;

    public void sendSms(String toPhone, String body) {

        if (toPhone == null || toPhone.isBlank()) {
            throw new IllegalArgumentException("Recipient phone number cannot be empty");
        }

        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("SMS body cannot be empty");
        }

        String formattedPhone = formatToE164(toPhone);

        try {

            Message message = Message.creator(new PhoneNumber(formattedPhone), new PhoneNumber(twilioConfig.getPhoneNumber()), body).create();
            log.info(
                    "SMS sent successfully | to={} | sid={} | status={}",
                    maskPhoneNumber(formattedPhone),
                    message.getSid(),
                    message.getStatus());
        } catch (Exception ex) {
            log.error("Failed to send SMS | to={} | error={}", maskPhoneNumber(formattedPhone), ex.getMessage(), ex);
            throw new RuntimeException("Failed to send SMS to " + formattedPhone,ex);
        }
    }

    private String formatToE164(String phoneNumber) {
        String phone = phoneNumber.trim()
                .replaceAll("[^\\d+]", "");

        if (phone.isBlank()) {
            throw new IllegalArgumentException("Invalid phone number: " + phoneNumber);
        }
        if (phone.startsWith("+")) {
            return phone;
        }
        if (phone.startsWith("91") && phone.length() == 12) {
            return "+" + phone;
        }

        if (phone.length() == 10) {
            return "+91" + phone;
        }
        throw new IllegalArgumentException("Invalid Indian phone number format: " + phoneNumber);
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 6) {
            return "******";
        }

        return phoneNumber.substring(0, 3) + "******"
                + phoneNumber.substring(phoneNumber.length() - 2);
    }
}