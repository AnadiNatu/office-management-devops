package com.office_management.auth_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String fname;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lname;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100)
    private String password;

    @Size(max = 20)
    private String phoneNumber;
}