package com.office_management.auth_service.controller;

import com.office_management.auth_service.service.interfaces.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2Service oauth2Service;

    @GetMapping("/user-info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal OAuth2User oauthUser) {

        if (oauthUser == null) {

            return ResponseEntity.badRequest().body(Map.of(
                                    "message",
                                    "No active OAuth2 session"));
        }

        return ResponseEntity.ok(
                Map.of("provider", oauth2Service.determineProvider(oauthUser),
                        "attributes", oauthUser.getAttributes()));
    }

    @GetMapping("/failure")
    public ResponseEntity<?> failure(@RequestParam(defaultValue = "OAuth2 authentication failed") String error) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "OAuth2 authentication failed",
                                "message", error));
    }
}
// Need to expose the "login" endpoint for the OAuth2 as well