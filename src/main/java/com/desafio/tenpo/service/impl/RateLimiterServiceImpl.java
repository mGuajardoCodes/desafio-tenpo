package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.config.properties.RateLimiterConfig;
import com.desafio.tenpo.service.RateLimiterService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@AllArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RateLimiterConfig config;

    @Override
    public Mono<Boolean> isAllowed(String clientId) {
        String key = "rate-limiter:" + clientId;
        int limit = config.getLimit();
        Duration window = config.getWindow();

        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(requestCount -> Mono.just(requestCount == 1)
                        .flatMap(isFirstRequest -> isFirstRequest
                                ? redisTemplate.expire(key, window).thenReturn(true)
                                : Mono.just(requestCount <= limit)
                        ));
    }
}
