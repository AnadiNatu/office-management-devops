package com.office_management.auth_service.service.rabbit;

import com.office_management.auth_service.dto.kafka.LoginNotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "office.auth.exchange";

    private static final String ROUTING_KEY ="user.login";

    public void publishLoginEvent(Long userId, String email, String fname, String phoneNumber) {

        LoginNotificationMessage message = LoginNotificationMessage.builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType("USER_LOGIN")
                        .source("auth-service")
                        .occurredAt(LocalDateTime.now())
                        .userId(userId)
                        .email(email)
                        .fname(fname)
                        .phoneNumber(phoneNumber)
                        .build();

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
    }

}