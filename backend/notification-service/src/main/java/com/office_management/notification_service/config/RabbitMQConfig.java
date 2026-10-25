package com.office_management.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {

    // EXCHANGE
    public static final String TASK_EXCHANGE = "office.task.exchange";
    public static final String MESSAGE_EXCHANGE = "office.message.exchange";
    public static final String MESSAGE_DEAD_LETTER_EXCHANGE = "office.notification.dlx";
    public static final String COMMENT_EXCHANGE = "office.comment.exchange";
    public static final String COMMENT_DEAD_LETTER_EXCHANGE = "office.notification.dlx";

    // QUEUES
    public static final String TASK_CREATED_QUEUE = "office.notification.created.queue";
    public static final String TASK_ASSIGNED_QUEUE = "office.notification.assigned.queue";
    public static final String TASK_REASSIGNED_QUEUE = "office.notification.reassigned.queue";
    public static final String TASK_STATUS_UPDATED_QUEUE = "office.notification.status.updated.queue";
    public static final String TASK_COMPLETED_QUEUE = "office.notification.completed.queue";
    public static final String TASK_DELETED_QUEUE = "office.notification.deleted.queue";

    public static final String MESSAGE_NOTIFICATION_QUEUE = "office.notification.message.received.queue";
    public static final String MESSAGE_DEAD_LETTER_QUEUE = "office.notification.message.received.dlq";


    public static final String COMMENT_CREATED_QUEUE = "office.notification.comment.created.queue";
    public static final String COMMENT_DEAD_LETTER_QUEUE = "office.notification.comment.created.dlq";

    // ROUTING KEYS

    public static final String TASK_CREATED_ROUTING_KEY = "task.created";
    public static final String TASK_ASSIGNED_ROUTING_KEY = "task.assigned";
    public static final String TASK_REASSIGNED_ROUTING_KEY = "task.reassigned";
    public static final String TASK_STATUS_UPDATED_ROUTING_KEY = "task.status.updated";
    public static final String TASK_COMPLETED_ROUTING_KEY = "task.completed";
    public static final String TASK_DELETED_ROUTING_KEY = "task.deleted";
    public static final String MESSAGE_SENT_ROUTING_KEY = "message.sent";

    public static final String MESSAGE_DEAD_LETTER_ROUTING_KEY = "message.received.dead";
    public static final String COMMENT_CREATED_ROUTING_KEY = "comment.created";

    public static final String COMMENT_DEAD_LETTER_ROUTING_KEY = "comment.created.dead";

    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(TASK_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange messageExchange(){
        return new TopicExchange(MESSAGE_EXCHANGE , true , false);
    }

    @Bean
    public TopicExchange commentExchange() {
        return new TopicExchange(COMMENT_EXCHANGE, true, false);
    }

    // QUEUES
    @Bean
    public Queue taskCreatedQueue() {
        return QueueBuilder.durable(TASK_CREATED_QUEUE).build();
    }

    @Bean
    public Queue taskAssignedQueue() {
        return QueueBuilder.durable(TASK_ASSIGNED_QUEUE).build();
    }

    @Bean
    public Queue taskReassignedQueue() {
        return QueueBuilder.durable(TASK_REASSIGNED_QUEUE).build();
    }

    @Bean
    public Queue taskStatusUpdatedQueue() {
        return QueueBuilder.durable(TASK_STATUS_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue taskCompletedQueue() {
        return QueueBuilder.durable(TASK_COMPLETED_QUEUE).build();
    }

    @Bean
    public Queue taskDeletedQueue() {
        return QueueBuilder.durable(TASK_DELETED_QUEUE).build();
    }

    // BINDINGS

    @Bean
    public Binding taskCreatedBinding(Queue taskCreatedQueue, TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskCreatedQueue)
                .to(taskExchange)
                .with(TASK_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskAssignedBinding(Queue taskAssignedQueue, TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskAssignedQueue)
                .to(taskExchange)
                .with(TASK_ASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding taskReassignedBinding(Queue taskReassignedQueue, TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskReassignedQueue)
                .to(taskExchange)
                .with(TASK_REASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding taskStatusUpdatedBinding(Queue taskStatusUpdatedQueue, TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskStatusUpdatedQueue)
                .to(taskExchange)
                .with(TASK_STATUS_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskCompletedBinding(Queue taskCompletedQueue, TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskCompletedQueue)
                .to(taskExchange)
                .with(TASK_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding taskDeletedBinding(Queue taskDeletedQueue, TopicExchange taskExchange) {

        return BindingBuilder
                .bind(taskDeletedQueue)
                .to(taskExchange)
                .with(TASK_DELETED_ROUTING_KEY);
    }

    @Bean
    public DirectExchange messageDeadLetterExchange() {
        return new DirectExchange(
                MESSAGE_DEAD_LETTER_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue messageNotificationQueue() {
        return QueueBuilder.durable(MESSAGE_NOTIFICATION_QUEUE)
                .withArgument(
                        "x-dead-letter-exchange",
                        MESSAGE_DEAD_LETTER_EXCHANGE
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        MESSAGE_DEAD_LETTER_ROUTING_KEY
                )
                .build();
    }

    @Bean
    public Queue messageNotificationDeadLetterQueue() {
        return QueueBuilder.durable(MESSAGE_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding messageNotificationBinding(Queue messageNotificationQueue, TopicExchange messageExchange) {

        return BindingBuilder
                .bind(messageNotificationQueue)
                .to(messageExchange)
                .with(MESSAGE_SENT_ROUTING_KEY);
    }

    @Bean
    public Binding messageNotificationDeadLetterBinding(Queue messageNotificationDeadLetterQueue, DirectExchange messageDeadLetterExchange) {

        return BindingBuilder
                .bind(messageNotificationDeadLetterQueue)
                .to(messageDeadLetterExchange)
                .with(MESSAGE_DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public DirectExchange commentDeadLetterExchange() {
        return new DirectExchange(COMMENT_DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue commentCreatedQueue() {
        return QueueBuilder.durable(COMMENT_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", COMMENT_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", COMMENT_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue commentDeadLetterQueue() {
        return QueueBuilder.durable(COMMENT_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding commentCreatedBinding(@Qualifier("commentCreatedQueue") Queue queue, @Qualifier("commentExchange") TopicExchange exchange) {

        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(COMMENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding commentDeadLetterBinding(
            @Qualifier("commentDeadLetterQueue") Queue queue,
            @Qualifier("commentDeadLetterExchange") DirectExchange exchange) {

        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(COMMENT_DEAD_LETTER_ROUTING_KEY);
    }


    // ============================================================
    // MESSAGE CONVERTER
    // ============================================================

    @Bean
    public MessageConverter messageConverter() {

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);

        return converter;
    }


    // ============================================================
    // LISTENER CONTAINER
    // ============================================================

    @Bean
    public SimpleRabbitListenerContainerFactory
    rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}