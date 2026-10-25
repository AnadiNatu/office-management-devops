package com.office_management.auth_service.controller;


import com.office_management.auth_service.dto.EmailOtpRequest;
import com.office_management.auth_service.dto.ResetPasswordRequest;
import com.office_management.auth_service.service.interfaces.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/password-reset/")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody EmailOtpRequest request) {
        passwordResetService.requestReset(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Password reset OTP sent successfully"));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }
}
// Hey chat the PasswordRest and the PasswordController both has same request mapping and both have same endpoints exposed
// will not cause any ambiguous endpoint error.