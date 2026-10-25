package com.office_management.notification_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCountDTO {
    private long unreadCount;
}
