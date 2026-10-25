package com.office_management.email_service.service;


import com.office_management.email_service.dto.TaskEventMessage;

public interface EmailNotificationService {
    void processTaskEvent(TaskEventMessage event);
}