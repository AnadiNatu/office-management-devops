package com.office_management.notification_service.rabbitMQ_messaging;

import com.office_management.notification_service.config.RabbitMQConfig;
import com.office_management.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageNotificationConsumer {
    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.MESSAGE_NOTIFICATION_QUEUE)
    public void handleMessageReceived(MessageNotificationEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("Message notification event cannot be null");
        }

        log.info("Received message notification event. eventId={}, messageId={}, recipientUserId={}",
                event.getEventId(), event.getMessageId(), event.getRecipientUserId());

        // Let processing exceptions propagate so RabbitMQ can dead-letter
        // failed events instead of acknowledging them as successful.
        notificationService.createMessageNotification(event);
    }
}