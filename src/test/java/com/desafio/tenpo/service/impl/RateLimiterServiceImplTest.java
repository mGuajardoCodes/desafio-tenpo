package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.config.properties.RateLimiterProperties;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Integration tests for RateLimiterServiceImpl using Mockito to mock Redis interactions.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RateLimiterServiceImplTest {

    private AutoCloseable mocks;

    @Mock
    private ReactiveStringRedisTemplate redisTemplate;

    @Mock
    private RateLimiterProperties config;

    @Mock
    private ReactiveValueOperations<String, String> reactiveValueOps;

    @InjectMocks
    private RateLimiterServiceImpl rateLimiterService;

    private static final String CLIENT_ID = "test-client";
    private static final String KEY = "rate-limiter:" + CLIENT_ID;
    private static final int LIMIT = 3;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    @BeforeAll
    void init() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    void closeMocks() throws Exception {
        mocks.close();
    }

    @BeforeEach
    void setUp() {
        // Configure RateLimiterProperties mock
        when(config.getLimit()).thenReturn(LIMIT);
        when(config.getWindow()).thenReturn(WINDOW);

        // Configure redisTemplate.opsForValue() to return reactiveValueOps mock
        when(redisTemplate.opsForValue()).thenReturn(reactiveValueOps);
    }

    @Test
    @DisplayName("RateLimiter allows up to 3 requests and blocks the 4th")
    void testRateLimiterWithMultipleRequests() {
        // Arrange: Mock the increment behavior
        // For the first 3 calls, return 1, 2, 3
        // For the 4th call, return 4
        when(reactiveValueOps.increment(eq(KEY)))
                .thenReturn(Mono.just(1L)) // 1st call
                .thenReturn(Mono.just(2L)) // 2nd call
                .thenReturn(Mono.just(3L)) // 3rd call
                .thenReturn(Mono.just(4L)); // 4th call

        // Mock the expire behavior to return Mono.just(true) on the first request
        when(redisTemplate.expire(eq(KEY), eq(WINDOW)))
                .thenReturn(Mono.just(true));

        // Act & Assert: First call should allow
        Mono<Boolean> firstCall = rateLimiterService.isAllowed(CLIENT_ID);
        StepVerifier.create(firstCall)
                .expectNext(true)
                .verifyComplete();


        // Verify that expire was called on the first increment
        verify(redisTemplate, times(1)).expire(eq(KEY), eq(WINDOW));

        // Act & Assert: Second call should allow
        Mono<Boolean> secondCall = rateLimiterService.isAllowed(CLIENT_ID);
        StepVerifier.create(secondCall)
                .expectNext(true)
                .verifyComplete();

        // Act & Assert: Third call should allow
        Mono<Boolean> thirdCall = rateLimiterService.isAllowed(CLIENT_ID);
        StepVerifier.create(thirdCall)
                .expectNext(true)
                .verifyComplete();

        // Act & Assert: Fourth call should block
        Mono<Boolean> fourthCall = rateLimiterService.isAllowed(CLIENT_ID);
        StepVerifier.create(fourthCall)
                .expectNext(false)
                .verifyComplete();

        // Verify that increment was called four times
        verify(reactiveValueOps, times(4)).increment(eq(KEY));

        // Verify that expire was only called once
        verify(redisTemplate, times(1)).expire(eq(KEY), eq(WINDOW));
    }
}
