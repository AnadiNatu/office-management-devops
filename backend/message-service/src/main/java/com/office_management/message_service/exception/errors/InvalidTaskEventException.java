package com.office_management.message_service.exception.errors;

public class InvalidTaskEventException extends RuntimeException {
    public InvalidTaskEventException(String message) {
        super(message);
    }
}
