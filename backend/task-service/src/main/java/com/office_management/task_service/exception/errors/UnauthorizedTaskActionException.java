package com.office_management.task_service.exception.errors;

public class UnauthorizedTaskActionException extends RuntimeException {
    public UnauthorizedTaskActionException(String message) {
        super(message);
    }
}