package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.exceptions.NotFoundException;
import com.desafio.tenpo.service.RedisCacheService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.desafio.tenpo.config.RedisConfiguration.CACHE_NAME;
import static com.desafio.tenpo.config.RedisConfiguration.KEY_OF_PERCENTAGE;

@Service
@AllArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheServiceImpl.class);
    private CacheManager cacheManager;

    @Override
    public Mono<Double> getCachedValueInRedis() {
        log.info("Getting percentage value from redis cache");
        return Mono.defer(() -> getRedisCacheConfig()
                .flatMap(cache -> Mono.justOrEmpty(cache.get(KEY_OF_PERCENTAGE, Double.class)))
                // Throw error if cache value doesn't exist
                .switchIfEmpty(Mono.error(new NotFoundException("Cache value not found"))));
    }

    @Override
    public Mono<Void> updateCachedValueInRedis(Double percentageResponse) {
        log.info("Updating percentage value from redis cache");
        return getRedisCacheConfig()
                .doOnNext(cache -> cache.put(KEY_OF_PERCENTAGE, percentageResponse))
                .then();
    }

    // Throw error if cache configuration doesn't exist
    private Mono<Cache> getRedisCacheConfig() {
        return Mono.defer(() -> Mono.justOrEmpty(cacheManager.getCache(CACHE_NAME)))
                .switchIfEmpty(Mono.error(new NotFoundException("Cache config not found")));
    }
}
