package com.office_management.notification_service.service;


import com.office_management.notification_service.rabbitMQ_messaging.TaskEventMessage;

public interface TaskEventProcessingService {
    void process(TaskEventMessage event);
}