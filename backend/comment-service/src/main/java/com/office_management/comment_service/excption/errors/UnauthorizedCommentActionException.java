package com.office_management.comment_service.excption.errors;

public class UnauthorizedCommentActionException extends RuntimeException {
    public UnauthorizedCommentActionException(String message) {
        super(message);
    }
}
