package com.office_management.notification_service.entity;

import com.office_management.notification_service.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification" ,
indexes = {
@Index(name = "idx_notification_recipient" , columnList = "recipient_user_id"),
@Index(name = "idx_notification_read" , columnList = "recipient_user_id , is_read"),
@Index(name = "idx_notification_recipient_created" , columnList = "recipient_user_id , created_at"),
@Index(name = "idx_notification_task_id" , columnList = "task_id"),
@Index(name = "idx_notification_event_id" , columnList = "event_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40 )
    private NotificationType type;

    @Column(nullable = false, length = 200 )
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT" )
    private String message;

    @Column(name = "task_id" )
    private Long taskId;

    @Column(name = "event_id",nullable = false, length = 100)
    private String eventId;

    @Column(name = "is_read",nullable = false)
    private boolean read;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column( name = "read_at" )
    private LocalDateTime readAt;

    @PrePersist protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) { createdAt = now; }
        if (!read) { readAt = null; }
    }
}