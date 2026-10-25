package com.office_management.message_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // EXCHANGE
    public static final String TASK_EXCHANGE = "office.task.exchange";
    public static final String MESSAGE_EXCHANGE = "office.message.exchange";
    // QUEUES
    public static final String TASK_CREATED_QUEUE = "office.messaging.task.created.queue";
    public static final String TASK_ASSIGNED_QUEUE = "office.messaging.task.assigned.queue";
    public static final String TASK_REASSIGNED_QUEUE = "office.messaging.task.reassigned.queue";
    public static final String TASK_STATUS_UPDATED_QUEUE = "office.messaging.task.status.updated.queue";
    public static final String TASK_COMPLETED_QUEUE = "office.messaging.task.completed.queue";
    public static final String TASK_DELETED_QUEUE = "office.messaging.task.deleted.queue";

    // ROUTING KEYS
    public static final String TASK_CREATED_ROUTING_KEY = "task.created";
    public static final String TASK_ASSIGNED_ROUTING_KEY = "task.assigned";
    public static final String TASK_REASSIGNED_ROUTING_KEY = "task.reassigned";
    public static final String TASK_STATUS_UPDATED_ROUTING_KEY = "task.status.updated";
    public static final String TASK_COMPLETED_ROUTING_KEY = "task.completed";
    public static final String TASK_DELETED_ROUTING_KEY = "task.deleted";
    public static final String MESSAGE_SENT_ROUTING_KEY = "message.sent";


    // EXCHANGE
    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(TASK_EXCHANGE , true , false);
    }

    @Bean
    public TopicExchange messageExchange() {
        return new TopicExchange(MESSAGE_EXCHANGE , true , false);
    }


    // QUEUES
    @Bean
    public Queue taskCreatedQueue() {
        return new Queue(TASK_CREATED_QUEUE, true);
    }

    @Bean
    public Queue taskAssignedQueue() {
        return new Queue(TASK_ASSIGNED_QUEUE, true);
    }

    @Bean
    public Queue taskReassignedQueue() {
        return new Queue(TASK_REASSIGNED_QUEUE, true);
    }

    @Bean
    public Queue taskStatusUpdatedQueue() {
        return new Queue(TASK_STATUS_UPDATED_QUEUE, true);
    }

    @Bean
    public Queue taskCompletedQueue() {
        return new Queue(TASK_COMPLETED_QUEUE, true);
    }

    @Bean
    public Queue taskDeletedQueue() {
        return new Queue(TASK_DELETED_QUEUE, true);
    }


    // BINDINGS

    @Bean
    public Binding taskCreatedBinding() {
        return BindingBuilder
                .bind(taskCreatedQueue())
                .to(taskExchange())
                .with(TASK_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskAssignedBinding() {
        return BindingBuilder
                .bind(taskAssignedQueue())
                .to(taskExchange())
                .with(TASK_ASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding taskReassignedBinding() {
        return BindingBuilder
                .bind(taskReassignedQueue())
                .to(taskExchange())
                .with(TASK_REASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding taskStatusUpdatedBinding() {
        return BindingBuilder
                .bind(taskStatusUpdatedQueue())
                .to(taskExchange())
                .with(TASK_STATUS_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskCompletedBinding() {
        return BindingBuilder
                .bind(taskCompletedQueue())
                .to(taskExchange())
                .with(TASK_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding taskDeletedBinding() {
        return BindingBuilder
                .bind(taskDeletedQueue())
                .to(taskExchange())
                .with(TASK_DELETED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}
