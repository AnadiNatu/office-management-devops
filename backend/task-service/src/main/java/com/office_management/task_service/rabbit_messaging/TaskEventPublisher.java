package com.office_management.task_service.rabbit_messaging;

import com.office_management.task_service.config.RabbitMQConfig;
import com.office_management.task_service.entity.Task;
import com.office_management.task_service.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishTaskCreated(Task task, Long changedById) {
        TaskEventMessage message = buildMessage(task, TaskEventType.TASK_CREATED, changedById);
        publishAfterCommit(RabbitMQConfig.TASK_CREATED_ROUTING_KEY, message);
    }

    public void publishTaskAssigned(Task task, Long changedById) {
        TaskEventMessage message = buildMessage(task, TaskEventType.TASK_ASSIGNED, changedById);
        publishAfterCommit(RabbitMQConfig.TASK_ASSIGNED_ROUTING_KEY, message);
    }

    public void publishTaskReassigned(Task task, Long previousAssignedToId, Long changedById) {
        TaskEventMessage message = buildMessage(task, TaskEventType.TASK_REASSIGNED, changedById);
        message.setPreviousAssignedToId(previousAssignedToId);
        publishAfterCommit(RabbitMQConfig.TASK_REASSIGNED_ROUTING_KEY, message);
    }
    // Need a reassign method

    public void publishTaskStatusUpdated(Task task, TaskStatus previousStatus, Long changedById) {
        TaskEventMessage message = buildMessage(task, TaskEventType.TASK_STATUS_UPDATED, changedById);
        message.setPreviousStatus(previousStatus);
        message.setCurrentStatus(task.getStatus());
        publishAfterCommit(RabbitMQConfig.TASK_STATUS_UPDATED_ROUTING_KEY, message);
    }

    public void publishTaskCompleted(Task task,  TaskStatus previousStatus, Long changedById) {
        TaskEventMessage message = buildMessage(task, TaskEventType.TASK_COMPLETED, changedById);
        message.setPreviousStatus(previousStatus);
        message.setCurrentStatus(task.getStatus());
        publishAfterCommit(RabbitMQConfig.TASK_STATUS_COMPLETED_ROUTING_KEY, message);
    }

    public void publishTaskDeleted(Task task, Long changedById) {
        TaskEventMessage message = buildMessage(task, TaskEventType.TASK_DELETED, changedById);
        publishAfterCommit(RabbitMQConfig.TASK_DELETED_ROUTING_KEY, message);
    }

    private TaskEventMessage buildMessage(Task task, TaskEventType eventType, Long changedById) {
        return TaskEventMessage.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .source("task-service")
                .occurredAt(LocalDateTime.now())
                .taskId(task.getId())
                .title(task.getTitle())
                .createdById(task.getCreatedById())
                .assignedToId(task.getAssignedToId())
                .changedById(changedById)
                .currentStatus(task.getStatus())
                .build();
    }

    private void publish(String routingKey, TaskEventMessage message) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.TASK_EXCHANGE, routingKey, message);
            log.info("Task event published. eventId={}, eventType={}, taskId={}", message.getEventId(), message.getEventType(), message.getTaskId());
        } catch (Exception ex) {
            log.error("Failed to publish task event. eventId={}, eventType={}, taskId={}", message.getEventId(), message.getEventType(), message.getTaskId(), ex);
        }
    }

    private void publishAfterCommit(String routingKey , TaskEventMessage message){
        if (TransactionSynchronizationManager.isSynchronizationActive() &&
        TransactionSynchronizationManager.isActualTransactionActive()){
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            publish(routingKey, message);
                        }
                    }
            );
        }
    }
}