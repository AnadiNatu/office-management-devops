package com.office_management.notification_service.rabbitMQ_messaging;

import com.office_management.notification_service.enums.TaskEventType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEventMessage {

    private String eventId;
    private TaskEventType eventType;
    private String source;
    private LocalDateTime occurredAt;
    private Long taskId;
    private String title;
    private Long createdById;
    private Long assignedToId;
    private Long previousAssignedToId;
    private Long changedById;
    private String previousStatus;
    private String currentStatus;

}