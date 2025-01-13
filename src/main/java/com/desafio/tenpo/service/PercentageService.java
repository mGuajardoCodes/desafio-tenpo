package com.desafio.tenpo.service;

import com.desafio.tenpo.domain.PercentageDTO;
import reactor.core.publisher.Mono;

public interface PercentageService {
    Mono<Integer> calculatePercentage(Mono<PercentageDTO> request);
}
