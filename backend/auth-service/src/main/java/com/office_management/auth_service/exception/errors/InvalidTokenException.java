package com.office_management.auth_service.exception.errors;

public class InvalidTokenException  extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}