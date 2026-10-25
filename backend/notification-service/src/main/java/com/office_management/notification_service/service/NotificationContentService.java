package com.office_management.notification_service.service;

import com.office_management.notification_service.dto.NotificationContentDTO;
import com.office_management.notification_service.rabbitMQ_messaging.TaskEventMessage;

public interface NotificationContentService {
    NotificationContentDTO buildContent(TaskEventMessage event);
}