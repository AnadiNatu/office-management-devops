package com.office_management.auth_service.controller;

import com.office_management.auth_service.dto.ChangePasswordRequest;
import com.office_management.auth_service.dto.ResetPasswordRequest;
import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.security.UserDetailServiceImpl;
import com.office_management.auth_service.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

    private final AuthService authService;

    @PostMapping("/change")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request, @AuthenticationPrincipal Users userDetails) {
        if (userDetails == null || userDetails.getId() == null) {
            throw new IllegalStateException("Authenticated user information is unavailable");
        }
        authService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok("If an account exists with this email, a password reset OTP has been sent.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }
}