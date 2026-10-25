package com.office_management.comment_service.entity;

import com.office_management.comment_service.enums.UserRoles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_comments" ,
indexes = {
        @Index(name = "idx_comments_task_id" , columnList = "task_id"),
        @Index(name = "idx_comments_commented_by" , columnList = "commented_by_user_id"),
        @Index(name = "idx_comments_task_created" , columnList = "task_id , created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "commented_by_user_id", nullable = false)
    private Long commentedByUserId;

    @Column(name = "commenter_first_name", nullable = false, length = 100)
    private String commenterFirstName;

    @Column(name = "commenter_last_name", nullable = false, length = 100)
    private String commenterLastName;

//    @Column(name = "commenter_department", length = 150)
//    private String commenterDepartment;

    @Enumerated(EnumType.STRING)
    @Column(name = "commenter_role", nullable = false, length = 20)
    private UserRoles commenterRole;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}