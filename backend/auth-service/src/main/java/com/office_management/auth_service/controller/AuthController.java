package com.office_management.auth_service.controller;

import com.office_management.auth_service.dto.*;
import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.enums.UserRoles;
import com.office_management.auth_service.security.UserDetailServiceImpl;
import com.office_management.auth_service.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@Valid @RequestBody SignUpRequest request) {
        UserResponseDTO response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Need to integrate and unify the OAuth , Phone and normal login for a created users
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal Users userDetails) {
        if (userDetails == null || userDetails.getId() == null) {
            throw new IllegalStateException("Authenticated user information is unavailable");
        }
        return ResponseEntity.ok(authService.getCurrentUser(userDetails.getId()));
    }

    @PostMapping("/provision")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<UserResponseDTO> provisionUser(@Valid @RequestBody ProvisionUserRequest request, @AuthenticationPrincipal Users userDetails) {

        if (userDetails == null || userDetails.getId() == null) {
            throw new IllegalStateException("Authenticated user information is unavailable");
        }
        UserResponseDTO response = authService.provisionUser(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> changeRole(@PathVariable Long userId, @RequestParam UserRoles role) {
        UserResponseDTO updatedUser = authService.changeUserRole(userId, role);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth endpoint working!");
    }
}