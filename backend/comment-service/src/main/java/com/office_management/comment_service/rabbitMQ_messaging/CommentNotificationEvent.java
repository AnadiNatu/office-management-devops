package com.office_management.comment_service.rabbitMQ_messaging;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentNotificationEvent {
    private String eventId;
    private Long commentId;
    private Long taskId;
    private String taskTitle;

    private Long commenterUserId;
    private String commenterName;
    private String commenterRole;

    private String commentPreview;
    private List<Long> recipientUserIds;

    private LocalDateTime occurredAt;
}