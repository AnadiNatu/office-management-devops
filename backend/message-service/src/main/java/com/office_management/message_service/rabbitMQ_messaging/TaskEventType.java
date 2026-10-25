package com.office_management.message_service.rabbitMQ_messaging;

public enum TaskEventType {
        TASK_CREATED,
        TASK_ASSIGNED,
        TASK_REASSIGNED,
        TASK_STATUS_UPDATED,
        TASK_COMPLETED,
        TASK_DELETED
}
