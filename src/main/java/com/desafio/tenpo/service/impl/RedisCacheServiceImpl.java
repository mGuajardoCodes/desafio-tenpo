package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.exceptions.NotFoundException;
import com.desafio.tenpo.service.RedisCacheService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.desafio.tenpo.config.RedisConfiguration.KEY_OF_PERCENTAGE;

@Service
@AllArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheServiceImpl.class);
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Override
    public Mono<Double> getCachedValueInRedis() {
        log.info("Getting percentage value from redis cache");
        return reactiveRedisTemplate.opsForValue()
                .get(KEY_OF_PERCENTAGE)
                .cast(Double.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Cache value not found"))));
    }

    @Override
    public Mono<Void> updateCachedValueInRedis(Double percentageResponse) {
        log.info("Updating percentage value in redis cache");
        return reactiveRedisTemplate.opsForValue()
                .set(KEY_OF_PERCENTAGE, percentageResponse)
                .doOnError(throwable -> log.warn("Cached value could not be saved"))
                .then();
    }
}
