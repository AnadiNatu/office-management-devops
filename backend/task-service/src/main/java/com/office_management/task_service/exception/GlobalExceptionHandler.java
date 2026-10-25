package com.office_management.task_service.exception;

import com.office_management.task_service.exception.errors.*;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<ErrorResponse> handleUserDisabled(UserDisabledException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedTaskActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAction(UnauthorizedTaskActionException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    @ExceptionHandler(InvalidTaskStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTaskStatus(InvalidTaskStatusException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(FeignServiceException.class)
    public ResponseEntity<ErrorResponse> handleFeignServiceException(FeignServiceException ex) {
        log.error("Feign communication failure", ex);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "Auth Service is currently unavailable");
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        log.error("Feign request failed. status={}", ex.status(), ex);

        if (ex.status() == 404) {
            return buildResponse(HttpStatus.NOT_FOUND, "Not Found", "Requested user was not found");
        }

        if (ex.status() == 401) {
            return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Internal authentication with Auth Service failed");
        }

        if (ex.status() == 403) {
            return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", "Task Service is not authorized to access Auth Service");
        }

        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "Auth Service is currently unavailable");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error in Task Service", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(
            AuthorizationDeniedException ex) {

        Map<String, Object> body = new HashMap<>();

        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", "You do not have permission to perform this action");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(body);
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