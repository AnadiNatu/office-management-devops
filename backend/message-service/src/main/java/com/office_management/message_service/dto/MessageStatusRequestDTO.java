package com.office_management.message_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class MessageStatusRequestDTO {
    @NotNull(message = "Message ID is required")
    @Positive(message = "Message ID must be positive")
    private Long messageId;
}