package com.desafio.tenpo.service;

import reactor.core.publisher.Mono;

public interface RateLimiterService {
    Mono<Boolean> isAllowed(String clientId);
}
