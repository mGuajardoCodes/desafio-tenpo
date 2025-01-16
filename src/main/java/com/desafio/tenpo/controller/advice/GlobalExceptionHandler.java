package com.desafio.tenpo.controller.advice;

import com.desafio.tenpo.exceptions.BadRequestException;
import com.desafio.tenpo.exceptions.ExternalServiceException;
import com.desafio.tenpo.exceptions.InternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ExternalServiceException.class)
    public Mono<Map<String, Object>> handleExternalServiceException(ExternalServiceException ex) {
        return logAndBuildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex.getCause());
    }

    @ExceptionHandler(InternalServiceException.class)
    public Mono<Map<String, Object>> handleInternalServiceException(InternalServiceException ex) {
        return logAndBuildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getCause());
    }

    @ExceptionHandler(BadRequestException.class)
    public Mono<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        return logAndBuildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getCause());
    }


    @ExceptionHandler(Exception.class)
    public Mono<Map<String, Object>> handleGeneralException(Exception ex) {
        String message = (ex.getCause() != null) ? ex.getCause().getMessage() : "An unexpected error occurred";
        return logAndBuildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, ex);
    }

    private Mono<Map<String, Object>> logAndBuildResponse(HttpStatus status,
                                                          String message,
                                                          Throwable cause) {

        log.error("{} with status: {}", message, status, cause);
        return Mono.defer(() -> {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", status.value());
            errorResponse.put("error", status.getReasonPhrase());
            errorResponse.put("message", message);
            return Mono.just(errorResponse);
        });
    }
}
