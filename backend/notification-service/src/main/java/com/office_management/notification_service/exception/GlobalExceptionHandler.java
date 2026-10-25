package com.office_management.notification_service.exception;

import com.office_management.notification_service.exception.errors.InvalidTaskEventException;
import com.office_management.notification_service.exception.errors.NotificationGenerationException;
import com.office_management.notification_service.exception.errors.NotificationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTaskEventException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTaskEventException(InvalidTaskEventException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(NotificationGenerationException.class)
    public ResponseEntity<ErrorResponse> handleNotificationGenerationException(NotificationGenerationException ex) {
        log.error("Feign communication failure", ex);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "Notification not ent");
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotificationNotFoundException(NotificationNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message) {

        ErrorResponse response = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(status.value())
                        .error(error)
                        .message(message)
                        .build();

        return ResponseEntity.status(status).body(response);
    }
}