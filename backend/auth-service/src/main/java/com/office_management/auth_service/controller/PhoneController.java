package com.office_management.auth_service.controller;


import com.office_management.auth_service.dto.OtpVerificationRequest;
import com.office_management.auth_service.dto.PhoneOtpRequest;
import com.office_management.auth_service.service.interfaces.PhoneOtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class PhoneController {
    private final PhoneOtpService phoneOtpService;

    @PostMapping("/phone/send")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody PhoneOtpRequest request) {
        phoneOtpService.sendOtp(request.getPhoneNumber());
        return ResponseEntity.ok(Map.of("message", "Phone OTP sent successfully"));
    }

    @PostMapping("/phone/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        phoneOtpService.verifyOtp(request.getIdentifier(), request.getOtp());
        return ResponseEntity.ok(Map.of("message", "Phone OTP verified successfully"));
    }
}
// Need to implement the twilio configuration for the sms and the otp services.
