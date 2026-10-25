package com.office_management.notification_service.service;

import com.office_management.notification_service.dto.NotificationCountDTO;
import com.office_management.notification_service.dto.NotificationResponseDTO;
import com.office_management.notification_service.dto.PageResponseDTO;
import com.office_management.notification_service.enums.NotificationType;
import com.office_management.notification_service.rabbitMQ_messaging.CommentNotificationEvent;
import com.office_management.notification_service.rabbitMQ_messaging.MessageNotificationEvent;
import com.office_management.notification_service.rabbitMQ_messaging.TaskEventMessage;

public interface NotificationService {
    NotificationResponseDTO createNotification(TaskEventMessage event, Long recipientUserId);

    PageResponseDTO<NotificationResponseDTO> getMyNotifications(Long currentUserId, int page, int size);
    PageResponseDTO<NotificationResponseDTO> getMyUnreadNotifications(Long currentUserId, int page, int size);

    NotificationCountDTO getUnreadCount(Long currentUserId);
    NotificationResponseDTO getNotificationById(Long notificationId, Long currentUserId);
    NotificationResponseDTO markAsRead(Long notificationId, Long currentUserId);
    long markAllAsRead(Long currentUserId);
    NotificationResponseDTO createMessageNotification(MessageNotificationEvent event);
    NotificationResponseDTO createCommentNotification(CommentNotificationEvent event, Long recipientUserId);
    PageResponseDTO<NotificationResponseDTO> getMyNotificationsByType(Long currentUserId, NotificationType type, int page, int size);
}