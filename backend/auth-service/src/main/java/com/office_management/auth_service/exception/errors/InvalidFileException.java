package com.office_management.auth_service.exception.errors;

public class InvalidFileException  extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }
}