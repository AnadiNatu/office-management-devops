package com.office_management.auth_service.exception.errors;

public class FeignServiceException extends RuntimeException {
    public FeignServiceException(String message) {
        super(message);
    }

    public FeignServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}