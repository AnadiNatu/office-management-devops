package com.office_management.notification_service.rabbitMQ_messaging;

import com.office_management.notification_service.config.RabbitMQConfig;
import com.office_management.notification_service.service.TaskEventProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventConsumer {

    private final TaskEventProcessingService taskEventProcessingService;

    @RabbitListener(queues = RabbitMQConfig.TASK_CREATED_QUEUE)
    public void handleTaskCreated(TaskEventMessage event ) {
        log.info( "Received TASK_CREATED event. eventId={}, taskId={}, title={}", event.getEventId(), event.getTaskId(), event.getTitle() );
        taskEventProcessingService.process(event);
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_ASSIGNED_QUEUE )
    public void handleTaskAssigned(TaskEventMessage event ) {
        log.info( "Received TASK_ASSIGNED event. eventId={}, taskId={}, assignedToId={}", event.getEventId(), event.getTaskId(), event.getAssignedToId());
        taskEventProcessingService.process(event);

    }

    @RabbitListener(queues = RabbitMQConfig.TASK_REASSIGNED_QUEUE )
    public void handleTaskReassigned(TaskEventMessage event ) {
        log.info( "Received TASK_REASSIGNED event. eventId={}, taskId={}, previousAssignedToId={}, assignedToId={}", event.getEventId(), event.getTaskId(), event.getPreviousAssignedToId(), event.getAssignedToId() );
        taskEventProcessingService.process(event);
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_STATUS_UPDATED_QUEUE)
    public void handleTaskStatusUpdated(TaskEventMessage event) {
        log.info( "Received TASK_STATUS_UPDATED event. eventId={}, taskId={}, previousStatus={}, currentStatus={}", event.getEventId(), event.getTaskId(), event.getPreviousStatus(), event.getCurrentStatus() );
        taskEventProcessingService.process(event);
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_COMPLETED_QUEUE)
    public void handleTaskCompleted(TaskEventMessage event ) {
        log.info( "Received TASK_COMPLETED event. eventId={}, taskId={}", event.getEventId(), event.getTaskId() );
        taskEventProcessingService.process(event);
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_DELETED_QUEUE)
    public void handleTaskDeleted(TaskEventMessage event ) {
        log.info( "Received TASK_DELETED event. eventId={}, taskId={}", event.getEventId(), event.getTaskId() );
        taskEventProcessingService.process(event);
    }
}