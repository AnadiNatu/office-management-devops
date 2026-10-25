package com.office_management.auth_service.controller;

import com.office_management.auth_service.dto.UserResponseDTO;
import com.office_management.auth_service.service.interfaces.AuthService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {
    private final AuthService authService;

    @PutMapping("/{userId}/disable")
    public ResponseEntity<UserResponseDTO> disableUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.disableUser(userId));
    }

    @PutMapping("/{userId}/enable")
    public ResponseEntity<UserResponseDTO> enableUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.enableUser(userId));
    }
}