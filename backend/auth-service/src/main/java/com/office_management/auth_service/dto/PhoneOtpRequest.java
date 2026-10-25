package com.office_management.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneOtpRequest {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}