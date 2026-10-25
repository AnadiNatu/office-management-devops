package com.office_management.message_service.dto;

import com.office_management.message_service.enums.MessageStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSummaryDTO {
    private Long id;
    private Long senderUserId;
    private String content;
    private MessageStatus status;
    private LocalDateTime createdAt;
}