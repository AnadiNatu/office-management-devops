package com.office_management.notification_service.service.impl;

import com.office_management.notification_service.dto.NotificationContentDTO;
import com.office_management.notification_service.dto.NotificationCountDTO;
import com.office_management.notification_service.dto.NotificationResponseDTO;
import com.office_management.notification_service.dto.PageResponseDTO;
import com.office_management.notification_service.entity.Notification;
import com.office_management.notification_service.enums.NotificationType;
import com.office_management.notification_service.exception.errors.NotificationGenerationException;
import com.office_management.notification_service.exception.errors.NotificationNotFoundException;
import com.office_management.notification_service.mapper.NotificationMapper;
import com.office_management.notification_service.rabbitMQ_messaging.CommentNotificationEvent;
import com.office_management.notification_service.rabbitMQ_messaging.MessageNotificationEvent;
import com.office_management.notification_service.rabbitMQ_messaging.TaskEventMessage;
import com.office_management.notification_service.repository.NotificationRepository;
import com.office_management.notification_service.service.NotificationContentService;
import com.office_management.notification_service.service.NotificationService;
import com.office_management.notification_service.websocket.NotificationWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationContentService notificationContentService;
    private final NotificationWebSocketService notificationWebSocketService;

    //    @Override
//    @Transactional
//    public NotificationResponseDTO createNotification(TaskEventMessage event, Long recipientUserId) {
//
//        validateEvent(event);
//        validateRecipient(recipientUserId);
//        if (notificationRepository.existsByEventIdAndRecipientUserId(event.getEventId(), recipientUserId)) {
//
//            log.info("Notification already exists. eventId={}, recipientUserId={}", event.getEventId(), recipientUserId);
////            return notificationRepository
////                    .findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId, PageRequest.of(0, 1))
////                    .stream()
////                    .filter(notification -> event
////                            .getEventId().equals(notification.getEventId()))
////                    .findFirst()
////                    .map(notificationMapper::toResponseDTO)
////                    .orElse(null);
//            return notificationRepository
//                    .findByEventIdAndRecipientUserId(event.getEventId(), recipientUserId)
//                    .map(notificationMapper::toResponseDTO)
//                    .orElse(null);
//        }
//
//        NotificationContentDTO content = notificationContentService.buildContent(event);
//
//        Notification notification = Notification.builder()
//                .recipientUserId(recipientUserId)
//                .type(content.getType())
//                .title(content.getTitle())
//                .message(content.getMessage())
//                .taskId(event.getTaskId())
//                .eventId(event.getEventId())
//                .read(false)
//                .build();
//
//        try {
//
//            Notification saved = notificationRepository.save(notification);
//
//            log.info("Notification created successfully. notificationId={}, eventId={}, recipientUserId={}", saved.getId(), event.getEventId(), recipientUserId);
//
//            return notificationMapper.toResponseDTO(saved);
//        } catch (DataIntegrityViolationException ex) {
//
//            /*
//             * Handles the race-condition case where two consumers
//             * simultaneously attempt to create the same notification.
//             *
//             * The database unique constraint remains the final
//             * protection against duplicates.
//             */
//            log.info("Duplicate notification prevented by database constraint. eventId={}, recipientUserId={}", event.getEventId(), recipientUserId);
//            return null;
//        }
//    }
@Override
@Transactional
public NotificationResponseDTO createNotification(TaskEventMessage event, Long recipientUserId) {

    validateEvent(event);
    validateRecipient(recipientUserId);

    if (notificationRepository.existsByEventIdAndRecipientUserId(event.getEventId(), recipientUserId)) {

        return notificationRepository
                .findByEventIdAndRecipientUserId(event.getEventId(), recipientUserId)
                .map(notificationMapper::toResponseDTO)
                .orElse(null);
    }

    NotificationContentDTO content = notificationContentService.buildContent(event);

    Notification notification = Notification.builder()
                    .recipientUserId(recipientUserId)
                    .type(content.getType())
                    .title(content.getTitle())
                    .message(content.getMessage())
                    .taskId(event.getTaskId())
                    .eventId(event.getEventId())
                    .read(false)
                    .build();

    Notification saved;

    try {
        saved = notificationRepository.save(notification);
    } catch (DataIntegrityViolationException ex) {

        log.info("Duplicate notification prevented. eventId={}, recipientUserId={}", event.getEventId(), recipientUserId);
        return notificationRepository
                .findByEventIdAndRecipientUserId(event.getEventId(), recipientUserId)
                .map(notificationMapper::toResponseDTO)
                .orElse(null);
    }
    NotificationResponseDTO response = notificationMapper.toResponseDTO(saved);
    // Database persistence is successful. Now attempt real-time delivery.
    try {
        notificationWebSocketService.sendNotification(recipientUserId, response);
    } catch (Exception ex) {
        // WebSocket failure must not undo notification persistence.
        log.error("Failed to send real-time notification. notificationId={}, userId={}", saved.getId(), recipientUserId, ex);
    }

    log.info("Notification created successfully. " + "notificationId={}, eventId={}, recipientUserId={}", saved.getId(),event.getEventId(), recipientUserId);
    return response;
}


    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<NotificationResponseDTO> getMyNotifications(Long currentUserId, int page, int size) {

        Pageable pageable = createPageable(page, size);

        Page<Notification> notificationPage =
                notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(currentUserId, pageable);

        return notificationMapper.toPageResponseDTO(
                notificationPage
        );
    }

    // ---------------------------------------------------------
    // GET MY UNREAD NOTIFICATIONS
    // ---------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<NotificationResponseDTO> getMyUnreadNotifications(Long currentUserId, int page, int size) {

        Pageable pageable = createPageable(page, size);

        Page<Notification> notificationPage = notificationRepository
                        .findByRecipientUserIdAndReadFalseOrderByCreatedAtDesc(currentUserId, pageable);

        return notificationMapper.toPageResponseDTO(
                notificationPage
        );
    }

    // ---------------------------------------------------------
    // GET UNREAD COUNT
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public NotificationCountDTO getUnreadCount(Long currentUserId) {
        long unreadCount = notificationRepository.countByRecipientUserIdAndReadFalse(currentUserId);
        return NotificationCountDTO.builder().unreadCount(unreadCount).build();
    }

    // GET ONE NOTIFICATION
    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(Long notificationId, Long currentUserId) {

        Notification notification = notificationRepository.findByIdAndRecipientUserId(notificationId, currentUserId).orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + notificationId));
        return notificationMapper.toResponseDTO(notification);
    }

    // MARK ONE AS READ

    @Override
    @Transactional
    public NotificationResponseDTO markAsRead(Long notificationId, Long currentUserId) {

        Notification notification = notificationRepository.findByIdAndRecipientUserId(notificationId, currentUserId).orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + notificationId));

        //If already read, simply return it.

        if (!notification.isRead()) {

            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }

        return notificationMapper.toResponseDTO(notification);
    }

    // MARK ALL AS READ
    @Override
    @Transactional
    public long markAllAsRead(Long currentUserId) {
        int updatedCount = notificationRepository.markAllAsRead(currentUserId, LocalDateTime.now());
        log.info("Marked {} notifications as read for user {}", updatedCount, currentUserId);
        return updatedCount;
    }

    @Override
    @Transactional
    public NotificationResponseDTO createMessageNotification(
            MessageNotificationEvent event) {

        if (event == null
                || event.getEventId() == null
                || event.getEventId().isBlank()
                || event.getMessageId() == null
                || event.getMessageId() <= 0
                || event.getConversationId() == null
                || event.getConversationId() <= 0
                || event.getSenderUserId() == null
                || event.getSenderUserId() <= 0
                || event.getRecipientUserId() == null
                || event.getRecipientUserId() <= 0
                || event.getSenderUserId().equals(event.getRecipientUserId())) {

            throw new NotificationGenerationException(
                    "Invalid message notification event"
            );
        }

        // Idempotency check: avoid creating the same notification
        // when RabbitMQ redelivers an event.
        var existing = notificationRepository
                .findByEventIdAndRecipientUserId(
                        event.getEventId(),
                        event.getRecipientUserId()
                );

        if (existing.isPresent()) {
            return notificationMapper.toResponseDTO(existing.get());
        }

        String preview = event.getContentPreview();

        if (preview == null || preview.isBlank()) {
            preview = "You have received a new message.";
        }

        Notification notification = Notification.builder()
                .recipientUserId(event.getRecipientUserId())
                .type(NotificationType.MESSAGE_RECEIVED)
                .title("New Message")
                .message(
                        "You received a message from user "
                                + event.getSenderUserId()
                                + ": "
                                + preview
                )
                // This is a message notification, not a task notification.
                .taskId(null)
                .eventId(event.getEventId())
                .read(false)
                .build();

        Notification saved = notificationRepository.saveAndFlush(notification);

        NotificationResponseDTO response =
                notificationMapper.toResponseDTO(saved);

        // Deliver the notification through Notification Service's
        // existing user-specific notification WebSocket.
        notificationWebSocketService.sendNotification(
                event.getRecipientUserId(),
                response
        );

        log.info(
                "Message notification created. notificationId={}, messageId={}, recipientUserId={}",
                saved.getId(),
                event.getMessageId(),
                event.getRecipientUserId()
        );

        return response;
    }

    // GET MY NOTIFICATIONS BY TYPE
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<NotificationResponseDTO> getMyNotificationsByType(Long currentUserId, NotificationType type, int page, int size) {
        Pageable pageable = createPageable(page, size);
        Page<Notification> notificationPage = notificationRepository.findByRecipientUserIdAndTypeOrderByCreatedAtDesc(currentUserId, type, pageable);
        return notificationMapper.toPageResponseDTO(notificationPage);
    }

    // PAGINATION HELPER
    private Pageable createPageable(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }


    @Override
    @Transactional
    public NotificationResponseDTO createCommentNotification(
            CommentNotificationEvent event,
            Long recipientUserId) {

        if (event == null
                || event.getEventId() == null
                || event.getEventId().isBlank()
                || event.getCommentId() == null
                || event.getCommentId() <= 0
                || event.getTaskId() == null
                || event.getTaskId() <= 0
                || event.getCommenterUserId() == null
                || event.getCommenterUserId() <= 0) {

            throw new NotificationGenerationException(
                    "Invalid comment notification event"
            );
        }

        if (recipientUserId == null || recipientUserId <= 0) {
            throw new NotificationGenerationException(
                    "A valid notification recipient is required"
            );
        }

        if (event.getRecipientUserIds() == null
                || !event.getRecipientUserIds().contains(recipientUserId)) {
            throw new NotificationGenerationException(
                    "Recipient is not part of the comment notification event"
            );
        }

        if (recipientUserId.equals(event.getCommenterUserId())) {
            throw new NotificationGenerationException(
                    "A commenter cannot receive their own comment notification"
            );
        }

        // Idempotency check for RabbitMQ redelivery.
        var existing = notificationRepository
                .findByEventIdAndRecipientUserId(
                        event.getEventId(),
                        recipientUserId
                );

        if (existing.isPresent()) {
            return notificationMapper.toResponseDTO(existing.get());
        }

        String taskTitle = event.getTaskTitle();

        if (taskTitle == null || taskTitle.isBlank()) {
            taskTitle = "Task #" + event.getTaskId();
        }

        String commenterName = event.getCommenterName();

        if (commenterName == null || commenterName.isBlank()) {
            commenterName = "A user";
        }

        String preview = event.getCommentPreview();

        if (preview == null || preview.isBlank()) {
            preview = "A new comment was added.";
        }

        Notification notification = Notification.builder()
                .recipientUserId(recipientUserId)
                .type(NotificationType.COMMENT_ADDED)
                .title("New Comment on Task")
                .message(
                        commenterName
                                + " commented on \""
                                + taskTitle
                                + "\": "
                                + preview
                )
                .taskId(event.getTaskId())
                .eventId(event.getEventId())
                .read(false)
                .build();

        Notification saved = notificationRepository.saveAndFlush(notification);

        NotificationResponseDTO response =
                notificationMapper.toResponseDTO(saved);

        // Real-time delivery is best-effort; the saved notification remains
        // available through the existing notification REST API.
        try {
            notificationWebSocketService.sendNotification(
                    recipientUserId,
                    response
            );
        } catch (Exception ex) {
            log.error(
                    "Failed to deliver comment notification over WebSocket. notificationId={}, recipientUserId={}",
                    saved.getId(),
                    recipientUserId,
                    ex
            );
        }

        log.info(
                "Comment notification saved. notificationId={}, commentId={}, recipientUserId={}",
                saved.getId(),
                event.getCommentId(),
                recipientUserId
        );

        return response;
    }


    private void validateEvent(TaskEventMessage event) {

        if (event == null) {
            throw new NotificationGenerationException("Task event cannot be null");
        }

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new NotificationGenerationException("Event ID is required");
        }

        if (event.getEventType() == null) {
            throw new NotificationGenerationException("Event type is required");
        }

        if (event.getTaskId() == null) {
            throw new NotificationGenerationException("Task ID is required");
        }
    }

    private void validateRecipient(Long recipientUserId) {
        if (recipientUserId == null) {
            throw new NotificationGenerationException("Recipient user ID is required");
        }

        if (recipientUserId <= 0) {
            throw new NotificationGenerationException("Recipient user ID must be positive");
        }
    }
}