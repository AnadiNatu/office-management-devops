package com.office_management.notification_service.exception.errors;

public class NotificationGenerationException extends RuntimeException {
    public NotificationGenerationException(String message) {
        super(message);
    }

    public NotificationGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
