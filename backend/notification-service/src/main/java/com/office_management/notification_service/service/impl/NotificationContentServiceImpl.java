package com.office_management.notification_service.service.impl;

import com.office_management.notification_service.dto.NotificationContentDTO;
import com.office_management.notification_service.enums.NotificationType;
import com.office_management.notification_service.exception.errors.NotificationGenerationException;
import com.office_management.notification_service.rabbitMQ_messaging.TaskEventMessage;
import com.office_management.notification_service.service.NotificationContentService;
import org.springframework.stereotype.Service;

@Service
public class NotificationContentServiceImpl  implements NotificationContentService {

    @Override
    public NotificationContentDTO buildContent(TaskEventMessage event) {

        if (event == null) {
            throw new NotificationGenerationException("Task event cannot be null");
        }

        if (event.getEventType() == null) {
            throw new NotificationGenerationException("Task event type is required");
        }

        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new NotificationGenerationException("Task title is required");
        }

        String taskTitle = event.getTitle();
        return switch (event.getEventType()) {

            case TASK_CREATED ->
                    NotificationContentDTO.builder()
                            .type(NotificationType.TASK_CREATED)
                            .title("Task Created")
                            .message("A new task has been created: \"" + taskTitle + "\"")
                            .build();

            case TASK_ASSIGNED ->
                    NotificationContentDTO.builder()
                            .type(NotificationType.TASK_ASSIGNED)
                            .title("Task Assigned")
                            .message("You have been assigned the task: \"" + taskTitle + "\"")
                            .build();

            case TASK_REASSIGNED ->
                    NotificationContentDTO.builder()
                            .type(NotificationType.TASK_REASSIGNED)
                            .title("Task Reassigned")
                            .message("The task \"" + taskTitle + "\" has been reassigned.")
                            .build();

            case TASK_STATUS_UPDATED ->
                    NotificationContentDTO.builder()
                            .type(NotificationType.TASK_STATUS_UPDATED)
                            .title("Task Status Updated")
                            .message("The status of task \"" + taskTitle + "\" has been updated to " + event.getCurrentStatus())
                            .build();

            case TASK_COMPLETED ->
                    NotificationContentDTO.builder()
                            .type(NotificationType.TASK_COMPLETED)
                            .title("Task Completed")
                            .message("The task \"" + taskTitle + "\" has been completed.")
                            .build();

            case TASK_DELETED ->
                    NotificationContentDTO.builder()
                            .type(NotificationType.TASK_DELETED)
                            .title("Task Deleted")
                            .message("The task \"" + taskTitle + "\" has been deleted.")
                            .build();
            default -> throw new NotificationGenerationException("Unsupported task event type: " + event.getEventType());
        };
    }
}