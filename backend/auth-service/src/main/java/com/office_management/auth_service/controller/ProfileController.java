package com.office_management.auth_service.controller;


import com.office_management.auth_service.dto.UserResponseDTO;
import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.service.interfaces.AuthService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final AuthService authService;

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> uploadProfilePhoto(
            @RequestParam("file") @NotNull MultipartFile file,
            @AuthenticationPrincipal Users userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        log.info("[PROFILE] Upload request | userId={}", userId);
        authService.uploadProfilePhoto(userId, file);
        return ResponseEntity.ok(authService.getCurrentUser(userId));
    }

    @GetMapping("/photo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getProfilePhoto(@AuthenticationPrincipal Users userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        UserResponseDTO user = authService.getCurrentUser(userId);
        String photo = user.getProfilePicture();
        return ResponseEntity.ok(photo != null ? photo : "");
    }

    @DeleteMapping("/photo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> removeProfilePhoto(@AuthenticationPrincipal Users userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        authService.removeProfilePhoto(userId);
        return ResponseEntity.ok("Profile photo removed successfully");
    }

    private Long getAuthenticatedUserId(Users userDetails) {
        if (userDetails == null || userDetails.getId() == null) {
            throw new IllegalStateException("Authenticated user information is unavailable");
        }
        return userDetails.getId();
    }
}
