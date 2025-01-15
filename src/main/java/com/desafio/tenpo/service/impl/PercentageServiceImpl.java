package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.config.properties.WebClientProperties;
import com.desafio.tenpo.domain.PercentageDTO;
import com.desafio.tenpo.exceptions.ExternalServiceException;
import com.desafio.tenpo.service.PercentageService;
import com.desafio.tenpo.service.RedisCacheService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@AllArgsConstructor
public class PercentageServiceImpl implements PercentageService {

    private static final Logger log = LoggerFactory.getLogger(PercentageServiceImpl.class);
    private final RedisCacheService redisCacheService;
    private final WebClient webClient;
    private final WebClientProperties config;

    public Mono<Integer> calculatePercentage(Mono<PercentageDTO> request) {
        return request.flatMap(entryRequest -> {


            int sum = entryRequest.getNum1() + entryRequest.getNum2();

            return callToGetPercentage().map(percentage -> {
                double calculatedPercentage = sum * (percentage / 100);
                return (int) Math.round(sum + calculatedPercentage);
            });

        });
    }

    public Mono<Double> callToGetPercentage() {
        return webClient.get()
                .uri("http://mockpercentage.cl")
                .retrieve()
                .bodyToMono(Double.class)
                .timeout(Duration.ofMillis(config.getTimeoutInMs()))
                .retry(config.getServiceCallMaxRetries())
                .flatMap(response -> redisCacheService.updateCachedValueInRedis(response).thenReturn(response))
                .onErrorResume(throwable -> {
                    log.error("External service failed, falling back to Redis cache");
                    return redisCacheService.getCachedValueInRedis()
                            .onErrorMap(cacheError -> {
                                log.warn(cacheError.getMessage());
                                return ExternalServiceException.builder()
                                        .message("External service responds with error")
                                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .build();
                            });
                });
    }
}