package com.office_management.email_service.exceptions.errors;

public class FeignServiceException extends RuntimeException {
    public FeignServiceException(String message) {
        super(message);
    }

    public FeignServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}