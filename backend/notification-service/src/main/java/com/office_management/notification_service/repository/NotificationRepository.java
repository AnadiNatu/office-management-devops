package com.office_management.notification_service.repository;

import com.office_management.notification_service.entity.Notification;
import com.office_management.notification_service.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId, Pageable pageable );
    Page<Notification> findByRecipientUserIdAndReadFalseOrderByCreatedAtDesc(Long recipientUserId, Pageable pageable);
    long countByRecipientUserIdAndReadFalse(Long recipientUserId );
    Optional<Notification> findByIdAndRecipientUserId(Long notificationId, Long recipientUserId );
    boolean existsByEventIdAndRecipientUserId(String eventId, Long recipientUserId );
    Page<Notification> findByTaskIdOrderByCreatedAtDesc(Long taskId, Pageable pageable );
    Page<Notification> findByRecipientUserIdAndTypeOrderByCreatedAtDesc(Long recipientUserId, NotificationType type, Pageable pageable );
    Optional<Notification> findByEventIdAndRecipientUserId(String eventId, Long recipientUserId);


    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.read = true,
            n.readAt = :readAt
        WHERE n.recipientUserId = :recipientUserId
          AND n.read = false
    """)
    int markAllAsRead(@Param("recipientUserId") Long recipientUserId, @Param("readAt") LocalDateTime readAt);
}