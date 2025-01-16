package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.domain.PercentageDTO;
import com.desafio.tenpo.exceptions.ExternalServiceException;
import com.desafio.tenpo.service.RedisCacheService;
import com.desafio.tenpo.config.properties.WebClientProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockito.Mockito.*;

/**
 * Integration tests for PercentageServiceImpl using MockServer to simulate external service.
 */
public class PercentageServiceImplTest {

    private static ClientAndServer mockServer;

    @Mock
    private RedisCacheService redisCacheService;

    @InjectMocks
    private PercentageServiceImpl percentageService;

    private static final int MOCKSERVER_PORT = 1080;

    @BeforeAll
    static void startMockServer() {
        // Initialize MockServer on specified port
        mockServer = ClientAndServer.startClientAndServer(MOCKSERVER_PORT);
    }

    @AfterAll
    static void stopMockServer() {
        // Stop MockServer after all tests
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Configure WebClient to point to MockServer
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + MOCKSERVER_PORT) // MockServer URL
                .build();

        // Configure WebClientProperties
        WebClientProperties config = new WebClientProperties();
        config.setTimeoutInMs(2000); // 2 seconds timeout
        config.setServiceCallMaxRetries(2); // Retry twice on failure

        // Inject dependencies into PercentageServiceImpl
        percentageService = new PercentageServiceImpl(redisCacheService, webClient, config);
    }

    @Test
    @DisplayName("Integration Test: calculatePercentage successfully retrieves percentage from external service")
    void calculatePercentageSuccess() {
        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("10.0")
                                .withHeader(new Header("Content-Type", "application/json"))
                );

        // Mock RedisCacheService to update cache successfully
        when(redisCacheService.updateCachedValueInRedis(10.0)).thenReturn(Mono.empty());

        PercentageDTO dto = new PercentageDTO();
        dto.setNum1(5);
        dto.setNum2(5);

        Mono<Integer> result = percentageService.calculatePercentage(Mono.just(dto));

        StepVerifier.create(result)
                .expectNext(11)
                .verifyComplete();

        // Verify that MockServer received the expected GET request
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/")
        );

        // Verify that RedisCacheService.updateCachedValueInRedis was called once with 10.0
        verify(redisCacheService, times(1)).updateCachedValueInRedis(10.0);
    }

    @Test
    @DisplayName("Integration Test: calculatePercentage handles external service failure and cache failure")
    void calculatePercentageFailure() {

        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/")
                )
                .respond(
                        response()
                                .withStatusCode(500)
                                .withBody("Internal Server Error")
                );

        // Mock RedisCacheService to fail retrieving cached value
        when(redisCacheService.getCachedValueInRedis()).thenReturn(Mono.error(new RuntimeException("Cache down")));


        PercentageDTO dto = new PercentageDTO();
        dto.setNum1(2);
        dto.setNum2(3);


        Mono<Integer> result = percentageService.calculatePercentage(Mono.just(dto));

        // Assert: Verify that an ExternalServiceException is thrown with the correct message and status
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ExternalServiceException &&
                                throwable.getMessage().equals("External service responds with error") &&
                                ((ExternalServiceException) throwable).getStatus() == HttpStatus.SERVICE_UNAVAILABLE)
                .verify();

        // Verify that MockServer received the expected GET request
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/")
        );

        // Verify that RedisCacheService.getCachedValueInRedis was called once
        verify(redisCacheService, times(1)).getCachedValueInRedis();
    }
}