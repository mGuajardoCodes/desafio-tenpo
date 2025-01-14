package com.desafio.tenpo.config;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.service.ApiCallLoggingService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ApiCallLoggingFilter implements WebFilter {

    private final ApiCallLoggingService apiCallLogService;
    private static final Logger log = LoggerFactory.getLogger(ApiCallLoggingFilter.class);

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        return chain.filter(exchange).doOnEach(signal -> {

            ApiCallLogEntity logEntity = new ApiCallLogEntity();
            logEntity.setTimestamp(LocalDateTime.now());
            logEntity.setEndpoint(request.getPath().toString());
            logEntity.setParameters(request.getQueryParams().toString());
            logEntity.setResponse(response.getStatusCode() != null ? response.getStatusCode().toString() : null);
            logEntity.setError(null);

            if (signal.isOnError()) {
                logEntity.setError(Optional.ofNullable(signal.getThrowable())
                        .map(Throwable::getMessage)
                        .orElse("Unknown error"));
            }

            apiCallLogService.saveCallHistory(logEntity)
                    .doOnError(error -> log.error("Error saving api call: {}", error.getMessage()))
                    .subscribe();
        });
    }
}
