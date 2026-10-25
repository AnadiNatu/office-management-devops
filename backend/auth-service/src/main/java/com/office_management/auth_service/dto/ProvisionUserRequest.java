package com.office_management.auth_service.dto;

import com.office_management.auth_service.enums.UserRoles;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvisionUserRequest {

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

    @Size(max = 20)
    private String phoneNumber;

    @NotBlank(message = "Initial password is required")
    @Size(min = 8, max = 100)
    private String password;

    @NotNull(message = "Role is required")
    private String role;

}