package com.office_management.email_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TASK_EXCHANGE = "office.task.exchange";

    public static final String TASK_CREATED_QUEUE = "office.email.task.created.queue";
    public static final String TASK_ASSIGNED_QUEUE = "office.email.task.assigned.queue";
    public static final String TASK_REASSIGNED_QUEUE = "office.email.task.reassigned.queue";
    public static final String TASK_STATUS_UPDATED_QUEUE = "office.email.task.status.updated.queue";
    public static final String TASK_COMPLETED_QUEUE = "office.email.task.completed.queue";
    public static final String TASK_DELETED_QUEUE = "office.email.task.deleted.queue";

    public static final String TASK_CREATED_ROUTING_KEY = "task.created";
    public static final String TASK_ASSIGNED_ROUTING_KEY = "task.assigned";
    public static final String TASK_REASSIGNED_ROUTING_KEY = "task.reassigned";
    public static final String TASK_STATUS_UPDATED_ROUTING_KEY = "task.status.updated";
    public static final String TASK_COMPLETED_ROUTING_KEY = "task.completed";
    public static final String TASK_DELETED_ROUTING_KEY = "task.deleted";

    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(TASK_EXCHANGE, true, false);
    }

    @Bean
    public Queue taskCreatedEmailQueue() {
        return QueueBuilder.durable(TASK_CREATED_QUEUE).build();
    }

    @Bean
    public Queue taskAssignedEmailQueue() {
        return QueueBuilder.durable(TASK_ASSIGNED_QUEUE).build();
    }

    @Bean
    public Queue taskReassignedEmailQueue() {
        return QueueBuilder.durable(TASK_REASSIGNED_QUEUE).build();
    }

    @Bean
    public Queue taskStatusUpdatedEmailQueue() {
        return QueueBuilder.durable(TASK_STATUS_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue taskCompletedEmailQueue() {
        return QueueBuilder.durable(TASK_COMPLETED_QUEUE).build();
    }

    @Bean
    public Queue taskDeletedEmailQueue() {
        return QueueBuilder.durable(TASK_DELETED_QUEUE).build();
    }

    @Bean
    public Binding taskCreatedBinding(Queue taskCreatedEmailQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskCreatedEmailQueue).to(taskExchange)
                .with(TASK_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskAssignedBinding(Queue taskAssignedEmailQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskAssignedEmailQueue).to(taskExchange)
                .with(TASK_ASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding taskReassignedBinding(Queue taskReassignedEmailQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskReassignedEmailQueue).to(taskExchange)
                .with(TASK_REASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding taskStatusUpdatedBinding(Queue taskStatusUpdatedEmailQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskStatusUpdatedEmailQueue).to(taskExchange)
                .with(TASK_STATUS_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskCompletedBinding(Queue taskCompletedEmailQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskCompletedEmailQueue).to(taskExchange)
                .with(TASK_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding taskDeletedBinding(Queue taskDeletedEmailQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskDeletedEmailQueue).to(taskExchange)
                .with(TASK_DELETED_ROUTING_KEY);
    }

    @Bean public MessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }

    @Bean public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory( ConnectionFactory connectionFactory, MessageConverter messageConverter ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory( connectionFactory );
        factory.setMessageConverter( messageConverter );
        return factory;
    }
}