package com.office_management.auth_service.dto;

import com.office_management.auth_service.enums.UserRoles;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String username;
    private String email;
    private String fname;
    private String lname;
    private UserRoles role;
    private String profilePicture;
    private String message;
}