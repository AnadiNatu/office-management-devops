package com.office_management.notification_service.service.impl;


import com.office_management.notification_service.enums.TaskEventType;
import com.office_management.notification_service.rabbitMQ_messaging.TaskEventMessage;
import com.office_management.notification_service.service.NotificationService;
import com.office_management.notification_service.service.TaskEventProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventProcessingServiceImpl implements TaskEventProcessingService {
    private final NotificationService notificationService;

    @Override
    public void process(TaskEventMessage event) {

        validateEvent(event);

        Set<Long> recipients = determineRecipients(event);

        if (recipients.isEmpty()) {
            log.info("No recipients found for event. eventId={}, eventType={}, taskId={}", event.getEventId(), event.getEventType(), event.getTaskId());
            return;
        }

        log.info("Generating notifications. eventId={}, eventType={}, recipients={}", event.getEventId(), event.getEventType(), recipients);

        for (Long recipientUserId : recipients) {
            try {
                notificationService.createNotification(event, recipientUserId);
            } catch (Exception ex) {
                log.error("Failed to generate notification. eventId={}, recipientUserId={}", event.getEventId(), recipientUserId, ex);
            }
        }
    }

    private Set<Long> determineRecipients(TaskEventMessage event) {
        Set<Long> recipients = new HashSet<>();
        TaskEventType eventType = event.getEventType();
        switch (eventType) {
            case TASK_CREATED -> {
                /*
                 * Creator should know that the task exists.
                 */
                addRecipient(recipients, event.getCreatedById());
                /*
                 * If task was created already assigned,
                 * notify the employee as well.
                 */
                addRecipient(recipients, event.getAssignedToId());
            }

            case TASK_ASSIGNED -> {
                addRecipient(recipients, event.getAssignedToId());
            }

            case TASK_REASSIGNED -> {
                // Notify both sides of the reassignment.
                addRecipient(recipients, event.getPreviousAssignedToId());
                addRecipient(recipients, event.getAssignedToId());
            }

            case TASK_STATUS_UPDATED -> {
                addRecipient(recipients, event.getCreatedById());
                addRecipient(recipients, event.getAssignedToId());
                removeActor(recipients, event.getChangedById());
            }

            case TASK_COMPLETED -> {
                addRecipient(recipients, event.getCreatedById());
                addRecipient(recipients, event.getAssignedToId());
                removeActor(recipients, event.getChangedById());
            }

            case TASK_DELETED -> {
                addRecipient(recipients, event.getCreatedById());
                addRecipient(recipients, event.getAssignedToId());
                removeActor(recipients, event.getChangedById());
            }
        }
        return recipients;
    }

    private void addRecipient(Set<Long> recipients, Long userId) {
        if (userId != null && userId > 0) {
            recipients.add(userId);
        }
    }

    private void removeActor(Set<Long> recipients, Long changedById) {
        if (changedById != null) {
            recipients.remove(changedById);
        }
    }

    private void validateEvent(TaskEventMessage event) {
        if (event == null) {
            throw new IllegalArgumentException("Task event cannot be null");
        }

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new IllegalArgumentException("Event ID is required");
        }

        if (event.getEventType() == null) {
            throw new IllegalArgumentException("Event type is required");
        }

        if (event.getTaskId() == null) {
            throw new IllegalArgumentException("Task ID is required");
        }
    }
}