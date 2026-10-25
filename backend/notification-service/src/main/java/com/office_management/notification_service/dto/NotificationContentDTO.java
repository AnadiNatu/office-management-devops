package com.office_management.notification_service.dto;

import com.office_management.notification_service.enums.NotificationType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationContentDTO {
    private NotificationType type;
    private String title;
    private String message;
}