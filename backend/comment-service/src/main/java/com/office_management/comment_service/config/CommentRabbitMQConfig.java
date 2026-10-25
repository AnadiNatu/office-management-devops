package com.office_management.comment_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommentRabbitMQConfig {

    public static final String COMMENT_EXCHANGE = "office.comment.exchange";
    public static final String COMMENT_CREATED_ROUTING_KEY = "comment.created";

    @Bean
    public TopicExchange commentExchange() {
        return new TopicExchange(COMMENT_EXCHANGE, true, false);
    }

    @Bean
    public MessageConverter commentMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}