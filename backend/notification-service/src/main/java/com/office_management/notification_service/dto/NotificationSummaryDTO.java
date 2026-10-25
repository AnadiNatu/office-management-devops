package com.office_management.notification_service.dto;

import com.office_management.notification_service.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSummaryDTO {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Long taskId;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}