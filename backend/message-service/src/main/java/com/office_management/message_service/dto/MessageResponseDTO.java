package com.office_management.message_service.dto;
import com.office_management.message_service.enums.MessageStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDTO {
    private Long id;
    private Long conversationId;
    private Long senderUserId;
    private Long recipientUserId;
    private String content;
    private MessageStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
}
