package com.desafio.tenpo.controller;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.service.ApiCallLoggingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiCallLoggingControllerTest {

    @Mock
    private ApiCallLoggingService apiCallLoggingService;

    @InjectMocks
    private ApiCallLoggingController apiCallLoggingController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Bind the controller to WebTestClient without starting a server
        webTestClient = WebTestClient.bindToController(apiCallLoggingController).build();
    }

    @Test
    @DisplayName("Should return a list of ApiCallLogEntity with default pagination")
    void testGetApiCallLogsDefaultPagination() {
        // Arrange: Create sample log entries
        ApiCallLogEntity log1 = new ApiCallLogEntity(1L, LocalDateTime.now(), "/api/logs",
                "num1=5&num2=5", "200 OK", null);
        ApiCallLogEntity log2 = new ApiCallLogEntity(2L, LocalDateTime.now(), "/api/logs",
                "num1=2&num2=3", "200 OK", null);
        List<ApiCallLogEntity> logs = Arrays.asList(log1, log2);

        // Mock the service to return the sample logs
        when(apiCallLoggingService.getHistoricalApiCalls(any(Pageable.class)))
                .thenReturn(Flux.fromIterable(logs));

        // Act: Perform GET request without pagination parameters
        webTestClient.get()
                .uri("/api/logs") // Default endpoint
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ApiCallLogEntity.class)
                .hasSize(2)
                .contains(log1, log2);

        // Assert: Verify that the service was called with default Pageable
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(apiCallLoggingService, times(1)).getHistoricalApiCalls(pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
    }
}
