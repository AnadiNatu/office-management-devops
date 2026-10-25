package com.office_management.message_service.rabbitMQ_messaging;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageNotificationEvent {
    private String eventId;
    private Long messageId;
    private Long conversationId;
    private Long senderUserId;
    private Long recipientUserId;
    private String contentPreview;
    private LocalDateTime occurredAt;
}