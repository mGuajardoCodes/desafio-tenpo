package com.desafio.tenpo.config.filter;

import com.desafio.tenpo.domain.ApiCallLogDTO;
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

            ApiCallLogDTO apiCallLogDTO = ApiCallLogDTO.builder()
                    .timestamp(LocalDateTime.now())
                    .endpoint(request.getPath().toString())
                    .parameters(request.getQueryParams().toString())
                    .response(response.getStatusCode() != null ? response.getStatusCode().toString() : null)
                    .error(null)
                    .build();


            if (signal.isOnError()) {
                apiCallLogDTO.setError(Optional.ofNullable(signal.getThrowable())
                        .map(Throwable::getMessage)
                        .orElse("Unknown error"));
            }

            apiCallLogService.saveCallHistory(apiCallLogDTO)
                    .doOnError(error -> log.error("Error saving api call: {}", error.getMessage()))
                    .subscribe();
        });
    }
}
