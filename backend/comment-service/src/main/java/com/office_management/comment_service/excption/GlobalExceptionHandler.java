package com.office_management.comment_service.excption;

import com.office_management.comment_service.excption.errors.*;
import feign.FeignException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignServiceException.class)
    public ResponseEntity<ErrorResponse> handleFeignServiceException(FeignServiceException ex , HttpServletRequest request) {
        log.error("Feign communication failure", ex);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "Auth Service is currently unavailable" ,request.getRequestURI());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(CommentNotFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Comment Not Found", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Task Not Found", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "User Not Found", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<ErrorResponse> handleUserDisabled(UserDisabledException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "User Disabled", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedCommentActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedCommentAction(UnauthorizedCommentActionException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException exception, HttpServletRequest request) {

        log.error("Feign request failed. Status: {}", exception.status());


        if (exception.status() == 404) {
            return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found",
                    "Requested downstream resource was not found", request.getRequestURI());
        }


        if (exception.status() == 401) {
            return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized",
                    "Downstream service rejected the request", request.getRequestURI());
        }

        if (exception.status() == 403) {
            return buildResponse(HttpStatus.FORBIDDEN, "Forbidden",
                    "Downstream service rejected the request", request.getRequestURI());
        }


        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable",
                "A required service is currently unavailable", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception,HttpServletRequest request) {

        String message = exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", message, request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        log.error("Unexpected error while processing request: {}", request.getRequestURI(), exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred", request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message , String path) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}