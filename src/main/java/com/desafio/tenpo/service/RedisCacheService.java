package com.desafio.tenpo.service;

import reactor.core.publisher.Mono;

public interface RedisCacheService {
    Mono<Double> getCachedValueInRedis();
    Mono<Void> updateCachedValueInRedis(Double percentageResponse);
}
