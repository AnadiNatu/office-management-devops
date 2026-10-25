package com.office_management.auth_service.config.rabbit;

import com.office_management.auth_service.dto.kafka.UserCreatedMessage;
import com.office_management.auth_service.dto.kafka.UserProfileUpdatedMessage;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String AUTH_EXCHANGE =
            "office.auth.exchange";

    public static final String USER_CREATED_QUEUE =
            "office.user.created.queue";

    public static final String USER_PROFILE_UPDATED_QUEUE =
            "office.user.profile.updated.queue";

    public static final String USER_CREATED_ROUTING_KEY =
            "user.created";

    public static final String USER_PROFILE_UPDATED_ROUTING_KEY =
            "user.profile.updated";

    public static final String LOGIN_NOTIFICATION_QUEUE =
            "office.login.notification.queue";

    @Bean
    public TopicExchange authExchange() {

        return new TopicExchange(
                AUTH_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue userCreatedQueue() {

        return QueueBuilder
                .durable(USER_CREATED_QUEUE)
                .build();
    }

    @Bean
    public Queue userProfileUpdatedQueue() {

        return QueueBuilder
                .durable(USER_PROFILE_UPDATED_QUEUE)
                .build();
    }

    @Bean
    public Queue loginNotificationQueue() {
        return QueueBuilder
                .durable(LOGIN_NOTIFICATION_QUEUE)
                .build();
    }

    @Bean
    public Binding userCreatedBinding(
            Queue userCreatedQueue,
            TopicExchange authExchange
    ) {

        return BindingBuilder
                .bind(userCreatedQueue)
                .to(authExchange)
                .with(USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding userProfileUpdatedBinding(
            Queue userProfileUpdatedQueue,
            TopicExchange authExchange
    ) {

        return BindingBuilder
                .bind(userProfileUpdatedQueue)
                .to(authExchange)
                .with(USER_PROFILE_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding loginNotificationBinding(
            Queue loginNotificationQueue,
            TopicExchange authExchange
    ) {

        return BindingBuilder
                .bind(loginNotificationQueue)
                .to(authExchange)
                .with("user.login");
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {

        DefaultClassMapper classMapper = new DefaultClassMapper();

        Map<String, Class<?>> idClassMapping = new HashMap<>();

        idClassMapping.put(
                "userCreatedMessage",
                UserCreatedMessage.class
        );

        idClassMapping.put(
                "userProfileUpdatedMessage",
                UserProfileUpdatedMessage.class
        );

        classMapper.setIdClassMapping(idClassMapping);

        Jackson2JsonMessageConverter converter =
                new Jackson2JsonMessageConverter();

        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {

        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(converter);

        return rabbitTemplate;
    }

}