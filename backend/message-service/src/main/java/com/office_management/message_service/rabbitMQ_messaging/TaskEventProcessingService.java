package com.office_management.message_service.rabbitMQ_messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskEventProcessingService {
    public void process(TaskEventMessage event) {
        if (event == null) {
            log.warn("Received null task event");
            return;
        }

        if (event.getEventType() == null) {
            log.warn("Received task event without eventType. eventId={}, taskId={}", event.getEventId(), event.getTaskId());
            return;
        }

        log.info("Processing task event. eventId={}, eventType={}, taskId={}, source={}", event.getEventId(), event.getEventType(), event.getTaskId(), event.getSource());

        switch (event.getEventType()) {

            case TASK_CREATED: processTaskCreated(event);
                break;
            case TASK_ASSIGNED: processTaskAssigned(event);
                break;
            case TASK_REASSIGNED: processTaskReassigned(event);
                break;
            case TASK_STATUS_UPDATED: processTaskStatusUpdated(event);
                break;
            case TASK_COMPLETED: processTaskCompleted(event);
                break;
            case TASK_DELETED: processTaskDeleted(event);
                break;
            default: log.warn("Unsupported task event type. eventId={}, eventType={}, taskId={}", event.getEventId(), event.getEventType(), event.getTaskId());
        }
    }

    private void processTaskCreated(TaskEventMessage event) {
        log.info("Processing TASK_CREATED. taskId={}, title={}, createdById={}, assignedToId={}", event.getTaskId(),
                event.getTitle(), event.getCreatedById(), event.getAssignedToId());

        // Future message-service specific processing can be added here.
    }


    private void processTaskAssigned(TaskEventMessage event) {
        log.info("Processing TASK_ASSIGNED. taskId={}, assignedToId={}, changedById={}",
                event.getTaskId(), event.getAssignedToId(), event.getChangedById());

        // Future message-service specific processing can be added here.
    }


    private void processTaskReassigned(TaskEventMessage event) {
        log.info("Processing TASK_REASSIGNED. taskId={}, previousAssignedToId={}, newAssignedToId={}, changedById={}",
                event.getTaskId(), event.getPreviousAssignedToId(), event.getAssignedToId(), event.getChangedById());

        // Future message-service specific processing can be added here.
    }


    private void processTaskStatusUpdated(TaskEventMessage event) {
        log.info("Processing TASK_STATUS_UPDATED. taskId={}, previousStatus={}, currentStatus={}, changedById={}",
                event.getTaskId(), event.getPreviousStatus(), event.getCurrentStatus(), event.getChangedById());

        // Future message-service specific processing can be added here.
    }


    private void processTaskCompleted(TaskEventMessage event) {
        log.info("Processing TASK_COMPLETED. taskId={}, createdById={}, assignedToId={}, changedById={}", event.getTaskId(), event.getCreatedById(), event.getAssignedToId(), event.getChangedById());
        // Future message-service specific processing can be added here.
    }


    private void processTaskDeleted(TaskEventMessage event) {

        log.info("Processing TASK_DELETED. taskId={}, createdById={}, assignedToId={}, changedById={}", event.getTaskId(), event.getCreatedById(), event.getAssignedToId(),
                event.getChangedById());
        // Future message-service specific processing can be added here.
    }
}