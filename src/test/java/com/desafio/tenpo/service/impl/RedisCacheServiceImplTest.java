package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.desafio.tenpo.config.RedisConfiguration.KEY_OF_PERCENTAGE;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RedisCacheServiceImpl using Mockito to mock Redis interactions.
 */
@ExtendWith(MockitoExtension.class)
public class RedisCacheServiceImplTest {

    @Mock
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> reactiveValueOperations;

    @InjectMocks
    private RedisCacheServiceImpl redisCacheService;

    private static final Double CACHED_VALUE = 75.5;

    @BeforeEach
    void setUp() {
        when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
    }

    @AfterEach
    void tearDown() {
        reset(reactiveRedisTemplate, reactiveValueOperations);
    }

    @Test
    @DisplayName("getCachedValueInRedis: Successfully retrieves cached value")
    void testGetCachedValueInRedis_Success() {
        when(reactiveValueOperations.get(KEY_OF_PERCENTAGE)).thenReturn(Mono.just(CACHED_VALUE));

        Mono<Double> result = redisCacheService.getCachedValueInRedis();

        StepVerifier.create(result)
                .expectNext(CACHED_VALUE)
                .verifyComplete();

        verify(reactiveValueOperations, times(1)).get(KEY_OF_PERCENTAGE);
    }

    @Test
    @DisplayName("getCachedValueInRedis: Throws NotFoundException when cached value is absent")
    void testGetCachedValueInRedis_CachedValueNotFound() {
        when(reactiveValueOperations.get(KEY_OF_PERCENTAGE)).thenReturn(Mono.empty());

        Mono<Double> result = redisCacheService.getCachedValueInRedis();

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Cache value not found"))
                .verify();

        verify(reactiveValueOperations, times(1)).get(KEY_OF_PERCENTAGE);
    }

    @Test
    @DisplayName("updateCachedValueInRedis: Successfully updates cached value")
    void testUpdateCachedValueInRedis_Success() {
        Double newPercentage = 85.0;
        when(reactiveValueOperations.set(KEY_OF_PERCENTAGE, newPercentage)).thenReturn(Mono.just(true));

        Mono<Void> result = redisCacheService.updateCachedValueInRedis(newPercentage);

        StepVerifier.create(result)
                .verifyComplete();

        verify(reactiveValueOperations, times(1)).set(KEY_OF_PERCENTAGE, newPercentage);
    }

    @Test
    @DisplayName("updateCachedValueInRedis: Throws Exception when Redis operation fails")
    void testUpdateCachedValueInRedis_Failure() {
        Double newPercentage = 85.0;
        when(reactiveValueOperations.set(KEY_OF_PERCENTAGE, newPercentage)).thenReturn(Mono.error(
                new RuntimeException("Redis failure")));

        Mono<Void> result = redisCacheService.updateCachedValueInRedis(newPercentage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Redis failure"))
                .verify();

        verify(reactiveValueOperations, times(1)).set(KEY_OF_PERCENTAGE, newPercentage);
    }
}
