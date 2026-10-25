package com.office_management.message_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessageRequestDTO {
    @NotBlank(message = "Message content is required")
    @Size(max = 5000, message = "Message cannot exceed 5000 characters")
    private String content;
}