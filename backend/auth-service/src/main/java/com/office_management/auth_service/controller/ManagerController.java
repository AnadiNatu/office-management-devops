package com.office_management.auth_service.controller;

import com.office_management.auth_service.dto.ProvisionUserRequest;
import com.office_management.auth_service.dto.UserResponseDTO;
import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.enums.UserRoles;
import com.office_management.auth_service.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/management/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
public class ManagerController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> provisionUser(@Valid @RequestBody ProvisionUserRequest request, @AuthenticationPrincipal Users userDetails) {
        UserResponseDTO response = authService.provisionUser(request, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    // NEW
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable UserRoles role) {
        return ResponseEntity.ok(authService.getUsersByRole(role));
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Authenticated user information is unavailable");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Users userDetails) {
            return userDetails.getId();
        }

        throw new IllegalStateException("Invalid authenticated user principal");
    }
}
// Need to endpoints to get list of the users apart from the admin and also to get list of the users based on roles