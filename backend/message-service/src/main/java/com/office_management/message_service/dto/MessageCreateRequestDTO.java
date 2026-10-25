package com.office_management.message_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageCreateRequestDTO {
    @NotBlank(message = "Message content is required")
    @Size(max = 5000, message = "Message cannot exceed 5000 characters")
    private String content;
}