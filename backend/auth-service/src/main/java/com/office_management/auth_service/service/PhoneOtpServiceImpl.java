package com.office_management.auth_service.service;

import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.exception.errors.UserNotFoundException;
import com.office_management.auth_service.repository.UserRepository;
import com.office_management.auth_service.service.interfaces.OtpService;
import com.office_management.auth_service.service.interfaces.PhoneOtpService;
import com.office_management.auth_service.service.interfaces.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneOtpServiceImpl implements PhoneOtpService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final SmsService smsService;

//    public void sendOtp(String phoneNumber) {
//        Users user = userRepository.findByPhoneNumber(phoneNumber.trim()).orElseThrow(() -> new UserNotFoundException("No user found with this phone number"));
//        String otp = otpService.generateAndStore("PHONE_VERIFICATION:" + user.getPhoneNumber());
//        System.out.println("PHONE OTP generated for testing: " + otp);
//    }

    @Override
    public void sendOtp(String phoneNumber) {

        String normalizedPhone = phoneNumber.trim();

        Users user = userRepository.findByPhoneNumber(normalizedPhone)
                .orElseThrow(() -> new UserNotFoundException("No user found with this phone number"));

        // OTP generation, storage, expiration, resend cooldown and attempt limit remain inside OtpService.
        String otp = otpService.generateAndStore("PHONE_VERIFICATION:" + user.getPhoneNumber());

        String message = "Your Office Management verification OTP is " + otp
                        + ". It is valid for 5 minutes. "
                        + "Do not share this OTP with anyone.";

        smsService.sendSms(user.getPhoneNumber(), message);
    }

    public void verifyOtp(String phoneNumber, String otp) {
        otpService.verify("PHONE_VERIFICATION:" + phoneNumber.trim(), otp);
    }
}
