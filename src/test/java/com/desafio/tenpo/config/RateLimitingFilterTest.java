package com.desafio.tenpo.config;

import com.desafio.tenpo.service.RateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RateLimitingFilterTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private WebFilterChain webFilterChain;

    @InjectMocks
    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFilter_Allowed() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-Client-Id", "client-123")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(rateLimiterService.isAllowed("client-123")).thenReturn(Mono.just(true));
        when(webFilterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = rateLimitingFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result).verifyComplete();
        verify(rateLimiterService).isAllowed("client-123");
        verify(webFilterChain).filter(exchange);
    }

    @Test
    void testFilter_NotAllowed() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-Client-Id", "client-123")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(rateLimiterService.isAllowed("client-123")).thenReturn(Mono.just(false));

        Mono<Void> result = rateLimitingFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result).verifyComplete();
        verify(rateLimiterService).isAllowed("client-123");

        MockServerHttpResponse response = exchange.getResponse();
        assert response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS;
        StepVerifier.create(response.getBodyAsString()).expectNext("You are exceeding the maximum requests per minute").verifyComplete();
    }

    @Test
    void testFilter_NoClientIdHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(rateLimiterService.isAllowed("default-client")).thenReturn(Mono.just(true));
        when(webFilterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = rateLimitingFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result).verifyComplete();
        verify(rateLimiterService).isAllowed("default-client");
        verify(webFilterChain).filter(exchange);
    }
}
