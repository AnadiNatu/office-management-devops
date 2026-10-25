package com.office_management.email_service.exceptions;

import com.office_management.email_service.exceptions.errors.EmailSendingException;
import com.office_management.email_service.exceptions.errors.FeignServiceException;
import com.office_management.email_service.exceptions.errors.UserNotFoundException;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandling {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "User Not Found", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(FeignServiceException.class)
    public ResponseEntity<ErrorResponse> handleFeignServiceException(FeignServiceException ex , HttpServletRequest request) {
        log.error("Feign communication failure", ex);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "Notification Service is currently unavailable" ,request.getRequestURI());
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponse> handleEmailSendingException(FeignServiceException ex , HttpServletRequest request) {
        log.error("Email was not sent", ex);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "Notification Service is currently unavailable" ,request.getRequestURI());
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