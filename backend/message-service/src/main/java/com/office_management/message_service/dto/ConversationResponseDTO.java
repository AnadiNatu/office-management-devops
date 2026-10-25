package com.office_management.message_service.dto;

import com.office_management.message_service.enums.ConversationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponseDTO {
    private Long id;
    private ConversationType type;
    private List<Long> participantUserIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}