package com.office_management.email_service.exceptions.errors;

public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String message) {
        super(message);
    }
    public EmailSendingException(String message, Throwable cause) { super(message, cause); }

}