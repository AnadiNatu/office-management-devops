package com.office_management.notification_service.rabbitMQ_messaging;

import com.office_management.notification_service.config.RabbitMQConfig;
import com.office_management.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentNotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.COMMENT_CREATED_QUEUE)
    public void handleCommentCreated(CommentNotificationEvent event) {

        if (event == null || event.getRecipientUserIds() == null || event.getRecipientUserIds().isEmpty()) {
            throw new IllegalArgumentException("Comment notification event has no recipients");
        }

        List<Long> recipients = event.getRecipientUserIds()
                .stream()
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        log.info("Received comment notification event. eventId={}, commentId={}, recipients={}"
                , event.getEventId(), event.getCommentId(), recipients);

        for (Long recipientUserId : recipients) {
            notificationService.createCommentNotification(event, recipientUserId);
        }
    }
}