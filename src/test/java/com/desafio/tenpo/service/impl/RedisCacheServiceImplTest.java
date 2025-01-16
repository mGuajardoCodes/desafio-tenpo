package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.desafio.tenpo.config.RedisConfiguration.CACHE_NAME;
import static com.desafio.tenpo.config.RedisConfiguration.KEY_OF_PERCENTAGE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RedisCacheServiceImpl using Mockito to mock Redis interactions.
 */
@ExtendWith(MockitoExtension.class)
public class RedisCacheServiceImplTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private RedisCacheServiceImpl redisCacheService;

    private static final String KEY = KEY_OF_PERCENTAGE;
    private static final Double CACHED_VALUE = 75.5;

    @BeforeEach
    void setUp() {
        // Any common setup can be done here
    }

    @AfterEach
    void tearDown() throws Exception {
        // Reset mocks after each test to ensure test isolation
        Mockito.reset(cacheManager, cache);
    }


    @Test
    @DisplayName("getCachedValueInRedis: Successfully retrieves cached value")
    void testGetCachedValueInRedis_Success() {
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
        when(cache.get(KEY, Double.class)).thenReturn(CACHED_VALUE);

        Mono<Double> result = redisCacheService.getCachedValueInRedis();

        StepVerifier.create(result)
                .expectNext(CACHED_VALUE)
                .verifyComplete();

        verify(cacheManager, times(1)).getCache(CACHE_NAME);
        verify(cache, times(1)).get(KEY, Double.class);
    }


    @Test
    @DisplayName("getCachedValueInRedis: Throws NotFoundException when cached value is absent")
    void testGetCachedValueInRedis_CachedValueNotFound() {

        when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
        // Simulate absent cache value
        when(cache.get(KEY, Double.class)).thenReturn(null);

        Mono<Double> result = redisCacheService.getCachedValueInRedis();

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Cache value not found"))
                .verify();

        verify(cacheManager, times(1)).getCache(CACHE_NAME);
        verify(cache, times(1)).get(KEY, Double.class);
    }


    @Test
    @DisplayName("getCachedValueInRedis: Throws NotFoundException when cache config is absent")
    void testGetCachedValueInRedis_CacheConfigNotFound() {
        // Simulate absent cache config
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(null);

        Mono<Double> result = redisCacheService.getCachedValueInRedis();

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Cache config not found"))
                .verify();

        verify(cacheManager, times(1)).getCache(CACHE_NAME);
        verify(cache, times(0)).get(anyString(), eq(Double.class)); // cache.get should not be called
    }


    @Test
    @DisplayName("updateCachedValueInRedis: Successfully updates cached value")
    void testUpdateCachedValueInRedis_Success() {

        Double newPercentage = 85.0;
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);

        Mono<Void> result = redisCacheService.updateCachedValueInRedis(newPercentage);

        StepVerifier.create(result)
                .verifyComplete();

        verify(cacheManager, times(1)).getCache(CACHE_NAME);
        verify(cache, times(1)).put(KEY, newPercentage);
    }

    @Test
    @DisplayName("updateCachedValueInRedis: Throws NotFoundException when cache config is absent")
    void testUpdateCachedValueInRedis_CacheConfigNotFound() {

        Double newPercentage = 85.0;
        // Simulate absent cache config
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(null);

        Mono<Void> result = redisCacheService.updateCachedValueInRedis(newPercentage);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Cache config not found"))
                .verify();

        verify(cacheManager, times(1)).getCache(CACHE_NAME);
        // cache.put should not be called
        verify(cache, times(0)).put(anyString(), any());
    }
}
