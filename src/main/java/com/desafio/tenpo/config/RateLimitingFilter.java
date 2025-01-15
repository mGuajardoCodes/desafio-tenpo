package com.desafio.tenpo.config;

import com.desafio.tenpo.service.RateLimiterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RateLimitingFilter implements WebFilter {

    private final RateLimiterService rateLimiterService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String clientId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Client-Id");

        clientId = Optional.ofNullable(clientId).orElse("default-client");

        return rateLimiterService.isAllowed(clientId)
                .flatMap(isAllowed -> isAllowed ? chain.filter(exchange) : handleTooManyRequests(exchange));
    }

    private Mono<Void> handleTooManyRequests(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        byte[] responseMessage = "Too Many Requests".getBytes();
        return exchange.getResponse().writeWith(Mono.just(exchange
                .getResponse()
                .bufferFactory()
                .wrap(responseMessage)));
    }
}
