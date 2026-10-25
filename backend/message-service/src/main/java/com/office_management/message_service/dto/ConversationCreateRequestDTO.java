package com.office_management.message_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationCreateRequestDTO {
    @NotNull(message = "Participant user ID is required")
    @Positive(message = "Participant user ID must be positive")
    private Long participantUserId;
}