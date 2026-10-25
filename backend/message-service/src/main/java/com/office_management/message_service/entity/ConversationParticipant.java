package com.office_management.message_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_participants" ,
uniqueConstraints = {
        @UniqueConstraint(name = "uk_conversation_user" , columnNames = {"conversation_id" , "user_id"})
},
indexes = {
        @Index(name = "idx_participants_user_id", columnList = "user_id"),
        @Index(name = "idx_participants_conversation_id", columnList = "conversation_id"),
        @Index(name = "idx_participants_user_conversation", columnList = "user_id, conversation_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id" , nullable = false)
    private Long conversationId;

    @Column(name = "user_id" , nullable = false)
    private Long userId;

    @Column(name = "joined_at" , nullable = false , updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate(){
        if (joinedAt == null){
            joinedAt = LocalDateTime.now();
        }
    }
}