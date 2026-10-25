package com.office_management.auth_service.dto.feign;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotificationRequest {
    @NotBlank
    @Email
    private String email;
    private String firstName;
    private String otp;
    private String eventType;

}