package com.office_management.auth_service.controller;


import com.office_management.auth_service.dto.feign.UserSummaryDTO;
import com.office_management.auth_service.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/internal")
@RequiredArgsConstructor
public class AuthInternalController {


    private final AuthService authService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserSummaryDTO> getUserById(@PathVariable Long userId) {

        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        var user = authService.getUserById(userId);

        UserSummaryDTO response = UserSummaryDTO.builder()
                        .id(user.getId())
                        .fname(user.getFname())
                        .lname(user.getLname())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getRole())
                        .profilePicture(user.getProfilePicture())
                        .enabled(user.getEnabled())
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/exists")
    public ResponseEntity<Boolean> userExists(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.ok(false);
        }

        try {
            authService.getUserById(userId);
            return ResponseEntity.ok(true);
        } catch (Exception ex) {
            return ResponseEntity.ok(false);
        }
    }
}