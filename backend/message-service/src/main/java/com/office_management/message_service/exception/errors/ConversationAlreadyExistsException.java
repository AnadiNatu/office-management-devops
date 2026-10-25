package com.office_management.message_service.exception.errors;

public class ConversationAlreadyExistsException extends RuntimeException {
    public ConversationAlreadyExistsException(String message) {
        super(message);
    }
}
