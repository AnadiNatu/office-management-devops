package com.office_management.message_service.rabbitMQ_messaging;

import com.office_management.message_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAfterCommit(MessageNotificationEvent event){
        if (event == null){
            throw new IllegalArgumentException("Message notification even cannot be null");
        }

        if (TransactionSynchronizationManager.isActualTransactionActive() && TransactionSynchronizationManager.isSynchronizationActive()){
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            publish(event);
                        }
                    }
            );
        }else{
            publish(event);
        }
    }

    private void publish(MessageNotificationEvent event){
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.MESSAGE_EXCHANGE,
                    RabbitMQConfig.MESSAGE_SENT_ROUTING_KEY,
                    event
            );

            log.info("Message notification event published. eventId={}, messageId={}, recipientUserId={}", event.getEventId(), event.getMessageId(), event.getRecipientUserId());
        }catch (Exception ex){
            log.error("Failed to publish message notification event. eventId={}, messageId={}",
                    event.getEventId(), event.getMessageId(), ex);
        }
    }

}