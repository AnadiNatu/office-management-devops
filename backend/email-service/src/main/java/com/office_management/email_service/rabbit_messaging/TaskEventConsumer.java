package com.office_management.email_service.rabbit_messaging;

import com.office_management.email_service.config.RabbitMQConfig;
import com.office_management.email_service.dto.TaskEventMessage;
import com.office_management.email_service.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventConsumer {

    private final EmailNotificationService emailNotificationService;

    @RabbitListener(queues = RabbitMQConfig.TASK_CREATED_QUEUE)
    public void handleTaskCreated(TaskEventMessage event) {
        log.info("Received TASK_CREATED event. " + "eventId={}, taskId={}, createdById={}", event.getEventId(), event.getTaskId(), event.getCreatedById());
        emailNotificationService.processTaskEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_ASSIGNED_QUEUE)
    public void handleTaskAssigned(TaskEventMessage event) {
        log.info("Received TASK_ASSIGNED event. " + "eventId={}, taskId={}, assignedToId={}",event.getEventId(), event.getTaskId(), event.getAssignedToId());
        emailNotificationService.processTaskEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_REASSIGNED_QUEUE)
    public void handleTaskReassigned(TaskEventMessage event) {
        log.info("Received TASK_REASSIGNED event. " + "eventId={}, taskId={}, " + "assignedToId={}, previousAssignedToId={}",
                event.getEventId(),
                event.getTaskId(),
                event.getAssignedToId(),
                event.getPreviousAssignedToId()
        );
        emailNotificationService.processTaskEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_STATUS_UPDATED_QUEUE)
    public void handleTaskStatusUpdated(TaskEventMessage event) {
        log.info("Received TASK_STATUS_UPDATED event. " + "eventId={}, taskId={}, " + "previousStatus={}, currentStatus={}",
                event.getEventId(),
                event.getTaskId(),
                event.getPreviousStatus(),
                event.getCurrentStatus()
        );
        emailNotificationService.processTaskEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_COMPLETED_QUEUE)
    public void handleTaskCompleted(TaskEventMessage event) {
        log.info("Received TASK_COMPLETED event. " + "eventId={}, taskId={}, assignedToId={}",
                event.getEventId(),
                event.getTaskId(),
                event.getAssignedToId());

        emailNotificationService.processTaskEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_DELETED_QUEUE)
    public void handleTaskDeleted(TaskEventMessage event) {
        log.info("Received TASK_DELETED event. " + "eventId={}, taskId={}, createdById={}",
                event.getEventId(),
                event.getTaskId(),
                event.getCreatedById()
        );
        emailNotificationService.processTaskEvent(event);
    }
}