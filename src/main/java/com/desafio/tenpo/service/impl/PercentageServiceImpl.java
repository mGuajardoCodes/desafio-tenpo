package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.domain.PercentageDTO;
import com.desafio.tenpo.exceptions.ExternalServiceException;
import com.desafio.tenpo.service.PercentageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

import static com.desafio.tenpo.config.RedisConfiguration.CACHE_NAME;
import static com.desafio.tenpo.config.RedisConfiguration.KEY_OF_PERCENTAGE;

@Service
public class PercentageServiceImpl implements PercentageService {

    private static final Logger log = LoggerFactory.getLogger(PercentageServiceImpl.class);

    @Autowired
    private WebClient webClient;

    @Autowired
    private CacheManager cacheManager;

    @Value("${external.service.max-retries:3}")
    private int maxRetries;

    @Override
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
                .onStatus(HttpStatusCode::is5xxServerError, this::buildErrorMsg)
                .bodyToMono(Double.class)
                .timeout(Duration.ofMillis(1000))
                .retry(maxRetries)
                .doOnSuccess(this::updateInRedis)
                .onErrorResume(throwable -> {
                    log.info("Getting percentage value from redis cache");
                    return Optional.ofNullable(cacheManager.getCache(CACHE_NAME))
                            .map(cache -> cache.get(KEY_OF_PERCENTAGE, Double.class))
                            .map(Mono::just)
                            .orElseGet(() -> Mono.error(
                                    new ExternalServiceException("External service responds with error, " +
                                            "and cannot be accessed in Redis cache")));
                });
    }


    private void updateInRedis(Double percentageResponse) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null)
            cache.put(KEY_OF_PERCENTAGE, percentageResponse);
    }

    private Mono<Throwable> buildErrorMsg(ClientResponse clientResponse) {
        String errorMsg = "External service responds with status " + clientResponse.statusCode();
        return Mono.error(new ExternalServiceException(errorMsg));
    }
}
