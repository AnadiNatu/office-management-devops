package com.office_management.task_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TASK_EXCHANGE = "office.task.exchange";

    public static final String TASK_CREATED_QUEUE = "office.task.created.queue";
    public static final String TASK_ASSIGNED_QUEUE = "office.task.assigned.queue";
    public static final String TASK_REASSIGNED_QUEUE = "office.task.reassigned.queue";
    public static final String TASK_STATUS_UPDATED_QUEUE = "office.task.status.updated.queue";
    public static final String TASK_COMPLETED_QUEUE = "office.task.completed.queue";
    public static final String TASK_DELETED_QUEUE = "office.task.deleted.queue";

    public static final String TASK_CREATED_ROUTING_KEY = "task.created";
    public static final String TASK_ASSIGNED_ROUTING_KEY = "task.assigned";
    public static final String TASK_REASSIGNED_ROUTING_KEY = "task.reassigned";
    public static final String TASK_STATUS_UPDATED_ROUTING_KEY = "task.status.updated";
    public static final String TASK_STATUS_COMPLETED_ROUTING_KEY = "task.completed";
    public static final String TASK_DELETED_ROUTING_KEY = "task.deleted";

    @Bean
    public TopicExchange taskExchange(){
        return new TopicExchange(TASK_EXCHANGE , true , false);
    }

    @Bean
    public Queue taskCreatedQueue(){
        return QueueBuilder.durable(TASK_CREATED_QUEUE)
                .build();
    }

    @Bean
    public Queue taskAssignedQueue(){
        return QueueBuilder.durable(TASK_ASSIGNED_QUEUE)
                .build();
    }

    @Bean
    public Queue taskReassignedQueue(){
        return QueueBuilder.durable(TASK_REASSIGNED_QUEUE)
                .build();
    }

    @Bean
    public Queue taskStatusUpdateQueue(){
        return QueueBuilder.durable(TASK_STATUS_UPDATED_QUEUE)
                .build();
    }

    @Bean
    public Queue taskCompletedQueue(){
        return QueueBuilder.durable(TASK_COMPLETED_QUEUE)
                .build();
    }

    @Bean
    public Queue taskDeletedQueue(){
        return QueueBuilder.durable(TASK_DELETED_QUEUE)
                .build();
    }

    @Bean
    public Binding taskCreatedBinding(@Qualifier("taskCreatedQueue") Queue taskCreatedQueue ,@Qualifier("taskExchange") TopicExchange taskExchange){
        return BindingBuilder.bind(taskCreatedQueue)
                .to(taskExchange)
                .with(TASK_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskAssiginedBinding(@Qualifier("taskAssignedQueue") Queue taskAssignedQueue ,@Qualifier("taskExchange") TopicExchange taskExchange){
        return BindingBuilder.bind(taskAssignedQueue)
                .to(taskExchange)
                .with(TASK_ASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding taskReassignedBinding(@Qualifier("taskReassignedQueue") Queue taskReassignedQueue,@Qualifier("taskExchange") TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskReassignedQueue)
                .to(taskExchange)
                .with(TASK_REASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding taskStatusUpdatedBinding(@Qualifier("taskStatusUpdateQueue") Queue taskStatusUpdatedQueue,@Qualifier("taskExchange")TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskStatusUpdatedQueue)
                .to(taskExchange)
                .with(TASK_STATUS_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskCompletedBinding(@Qualifier("taskCompletedQueue") Queue taskCompletedQueue,@Qualifier("taskExchange") TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskCompletedQueue)
                .to(taskExchange)
                .with(TASK_STATUS_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding taskDeletedBinding(@Qualifier("taskDeletedQueue") Queue taskDeletedQueue,@Qualifier("taskExchange")TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskDeletedQueue)
                .to(taskExchange)
                .with(TASK_DELETED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }
}