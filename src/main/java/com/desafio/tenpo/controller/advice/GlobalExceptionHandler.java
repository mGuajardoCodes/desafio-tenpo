package com.desafio.tenpo.controller.advice;

import com.desafio.tenpo.exceptions.BadRequestException;
import com.desafio.tenpo.exceptions.ExternalServiceException;
import com.desafio.tenpo.exceptions.InternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<Map<String, Object>> handleExternalServiceException(ExternalServiceException ex) {
        log.error("{} with status: {}", ex.getMessage(), ex.getStatus());
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex.getCause());
    }

    @ExceptionHandler(InternalServiceException.class)
    public ResponseEntity<Map<String, Object>> handleInternalServiceException(InternalServiceException ex) {
        log.error("{} with status: {}", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getCause());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        log.error("{} with status: {}", ex.getMessage(), HttpStatus.BAD_REQUEST);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getCause());
    }

    // I set cause error message in log for more description
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        log.error("{} with status: {}", ex.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, Throwable cause) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }
}

