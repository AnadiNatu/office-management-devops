package com.office_management.comment_service.rabbitMQ_messaging;

import com.office_management.comment_service.config.CommentRabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAfterCommit(CommentNotificationEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("Comment notification event cannot be null");
        }

        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            publish(event);
                        }
                    });
        } else {
            publish(event);
        }
    }

    private void publish(CommentNotificationEvent event) {
        try {
            rabbitTemplate.convertAndSend(CommentRabbitMQConfig.COMMENT_EXCHANGE,
                    CommentRabbitMQConfig.COMMENT_CREATED_ROUTING_KEY, event);

            log.info("Published comment notification event. eventId={}, commentId={}, recipients={}", event.getEventId(), event.getCommentId(), event.getRecipientUserIds());
        } catch (Exception ex) {
            log.error("Failed to publish comment notification event. eventId={}, commentId={}", event.getEventId(), event.getCommentId(), ex);
        }
    }
}