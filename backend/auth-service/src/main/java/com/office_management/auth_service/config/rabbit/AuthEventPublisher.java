package com.office_management.auth_service.config.rabbit;

import com.office_management.auth_service.dto.kafka.UserCreatedMessage;
import com.office_management.auth_service.dto.kafka.UserProfileUpdatedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishUserCreated(UserCreatedMessage message) {

        if (message == null) {
            log.warn("[RABBITMQ] USER_CREATED message is null");
            return;
        }

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.AUTH_EXCHANGE, RabbitMQConfig.USER_CREATED_ROUTING_KEY, message);
            log.info("[RABBITMQ] USER_CREATED published | userId={} | eventId={}", message.getUserId(), message.getEventId());
        } catch (Exception ex) {
            log.error("[RABBITMQ] USER_CREATED publish failed | userId={} | error={}", message.getUserId(), ex.getMessage(), ex);
        }
    }

    public void publishUserProfileUpdated(UserProfileUpdatedMessage message) {

        if (message == null) {
            log.warn("[RABBITMQ] USER_PROFILE_UPDATED message is null");
            return;
        }

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.AUTH_EXCHANGE, RabbitMQConfig.USER_PROFILE_UPDATED_ROUTING_KEY, message);

            log.info("[RABBITMQ] USER_PROFILE_UPDATED published | userId={} | eventId={}", message.getUserId(), message.getEventId());
        } catch (Exception ex) {
            log.error("[RABBITMQ] USER_PROFILE_UPDATED publish failed | userId={} | error={}", message.getUserId(), ex.getMessage(), ex);
        }
    }


}