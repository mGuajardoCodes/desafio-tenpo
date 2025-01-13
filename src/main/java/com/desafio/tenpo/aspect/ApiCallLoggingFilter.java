package com.desafio.tenpo.aspect;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.service.ApiCallLoggingService;
import lombok.AllArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class ApiCallLoggingFilter implements WebFilter {

    private final ApiCallLoggingService apiCallLogService;

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        return chain.filter(exchange)
                .doOnEach(signal -> {
                    if (signal.isOnComplete()) {
                        ApiCallLogEntity apiCallLogEntity = new ApiCallLogEntity();
                        apiCallLogEntity.setTimestamp(LocalDateTime.now());
                        apiCallLogEntity.setEndpoint(request.getPath().toString());
                        apiCallLogEntity.setParameters(request.getQueryParams().toString());
                        apiCallLogEntity.setResponse(response.getStatusCode() != null ?
                                response.getStatusCode().toString() : null);
                        // Arreglar
                        apiCallLogEntity.setError(null);

                        apiCallLogService.saveCallHistory(apiCallLogEntity)
                                .doOnError(error -> System.out.println("Error saving call history: "
                                        + error.getMessage()))
                                .subscribe();
                    }
                });
    }
}
