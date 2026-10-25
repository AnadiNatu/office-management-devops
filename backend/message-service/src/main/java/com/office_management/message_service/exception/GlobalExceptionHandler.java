package com.office_management.message_service.exception;

import com.office_management.message_service.exception.errors.*;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {

        return buildResponse(HttpStatus.NOT_FOUND , "Auth Service User not found" , "User not found");

    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<ErrorResponse> handleUserDisabled(UserDisabledException ex) {
        return buildResponse(HttpStatus.FORBIDDEN , "User is disabled by the admin" , "User is disabled");
    }

    @ExceptionHandler(AuthServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleAuthUnavailable(AuthServiceUnavailableException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE , "Auth Service is not available" , "Service not available");
    }

    @ExceptionHandler(InvalidConversationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidConversationException(AuthServiceUnavailableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST , "Invalid Conversation" , "You cannot create a conversation with yourself");
    }

    @ExceptionHandler(ConversationAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConversationAlreadyExistsException(ConversationAlreadyExistsException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST , "Conversation already exists" , "You cannot create a conversation");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST , "Invalid argument entered" , "Invalid Argument Exception");

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found" , "Can not find the resource");
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