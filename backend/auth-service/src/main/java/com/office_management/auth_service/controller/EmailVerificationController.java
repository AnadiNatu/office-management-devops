package com.office_management.auth_service.controller;

import com.office_management.auth_service.dto.EmailOtpRequest;
import com.office_management.auth_service.dto.OtpVerificationRequest;
import com.office_management.auth_service.service.interfaces.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/verify-email/send")
    public ResponseEntity<?> sendVerificationOtp(@Valid @RequestBody EmailOtpRequest request) {
        emailVerificationService.sendVerificationOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Verification OTP sent successfully"));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody OtpVerificationRequest request) {
        emailVerificationService.verifyEmail(request.getIdentifier(), request.getOtp());
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }
}