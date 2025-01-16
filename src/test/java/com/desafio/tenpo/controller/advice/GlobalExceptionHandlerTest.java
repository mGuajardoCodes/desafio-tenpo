package com.desafio.tenpo.controller.advice;


import com.desafio.tenpo.exceptions.BadRequestException;
import com.desafio.tenpo.exceptions.ExternalServiceException;
import com.desafio.tenpo.exceptions.InternalServiceException;
import com.desafio.tenpo.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Handle ExternalServiceException correctly")
    void handleExternalServiceException() {
        // Arrange
        ExternalServiceException ex = new ExternalServiceException("External service unavailable",
                HttpStatus.SERVICE_UNAVAILABLE);

        // Act
        Mono<Map<String, Object>> response = exceptionHandler.handleExternalServiceException(ex);

        // Assert
        StepVerifier.create(response)
                .consumeNextWith(map -> {
                    assertThat(map.get("status")).isEqualTo(503);
                    assertThat(map.get("error")).isEqualTo("Service Unavailable");
                    assertThat(map.get("message")).isEqualTo("External service unavailable");
                    assertThat(map.get("timestamp")).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Handle InternalServiceException correctly")
    void handleInternalServiceException() {
        // Arrange
        InternalServiceException ex = new InternalServiceException("Internal server error");

        // Act
        Mono<Map<String, Object>> response = exceptionHandler.handleInternalServiceException(ex);

        // Assert
        StepVerifier.create(response)
                .consumeNextWith(map -> {
                    assertThat(map.get("status")).isEqualTo(500);
                    assertThat(map.get("error")).isEqualTo("Internal Server Error");
                    assertThat(map.get("message")).isEqualTo("Internal server error");
                    assertThat(map.get("timestamp")).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Handle BadRequestException correctly")
    void handleBadRequestException() {
        // Arrange
        BadRequestException ex = new BadRequestException("Invalid input data");

        // Act
        Mono<Map<String, Object>> response = exceptionHandler.handleBadRequestException(ex);

        // Assert
        StepVerifier.create(response)
                .consumeNextWith(map -> {
                    assertThat(map.get("status")).isEqualTo(400);
                    assertThat(map.get("error")).isEqualTo("Bad Request");
                    assertThat(map.get("message")).isEqualTo("Invalid input data");
                    assertThat(map.get("timestamp")).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Handle general Exception correctly")
    void handleGeneralException() {
        // Arrange
        Exception ex = new Exception("Unexpected error");

        // Act
        Mono<Map<String, Object>> response = exceptionHandler.handleGeneralException(ex);

        // Assert
        StepVerifier.create(response)
                .consumeNextWith(map -> {
                    assertThat(map.get("status")).isEqualTo(500);
                    assertThat(map.get("error")).isEqualTo("Internal Server Error");
                    assertThat(map.get("message")).isEqualTo("An unexpected error occurred");
                    assertThat(map.get("timestamp")).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Handle NotFoundException correctly")
    void handleNotFoundException() {
        // Arrange: Create a NotFoundException instance
        NotFoundException ex = new NotFoundException("Resource not found");

        // Act: Invoke the handler method
        Mono<Map<String, Object>> response = exceptionHandler.handleNotFoundException(ex);

        // Assert: Verify the response map contents
        StepVerifier.create(response)
                .consumeNextWith(map -> {
                    assertThat(map.get("status")).isEqualTo(404);
                    assertThat(map.get("error")).isEqualTo("Not Found");
                    assertThat(map.get("message")).isEqualTo("Resource not found");
                    assertThat(map.get("timestamp")).isNotNull();
                })
                .verifyComplete();
    }
}
