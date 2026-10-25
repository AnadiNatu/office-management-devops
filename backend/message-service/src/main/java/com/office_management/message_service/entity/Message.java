package com.office_management.message_service.entity;

import com.office_management.message_service.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages" ,
indexes = {
 @Index(name = "idx_messages_conversation_id" , columnList = "conversation_id"),
 @Index(name = "idx_messages_sender_id" , columnList = "sender_user_id"),
 @Index(name = "idx_mssages_recipient_id" , columnList = "recipient_user_id"),
 @Index(name = "idx_messages_recipient_status" , columnList = "recipient_user_id , status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id",nullable = false)
    private Long conversationId;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = MessageStatus.SENT;
        }
    }
}