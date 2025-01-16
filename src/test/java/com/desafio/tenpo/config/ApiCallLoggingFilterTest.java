package com.desafio.tenpo.config;

import com.desafio.tenpo.config.filter.ApiCallLoggingFilter;
import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.service.ApiCallLoggingService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiCallLoggingFilterTest {

    @Mock
    private ApiCallLoggingService apiCallLoggingService;

    @Mock
    private WebFilterChain webFilterChain;

    @InjectMocks
    private ApiCallLoggingFilter apiCallLoggingFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFilter_SuccessfulRequest() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/test?param=value").build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        response.setStatusCode(HttpStatus.OK);
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(webFilterChain.filter(exchange)).thenReturn(Mono.empty());
        when(apiCallLoggingService.saveCallHistory(any(com.desafio.tenpo.domain.ApiCallLogDTO.class)))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = apiCallLoggingFilter.filter(exchange, webFilterChain);

        // Assert
        StepVerifier.create(result).verifyComplete();
        verify(apiCallLoggingService).saveCallHistory(any(com.desafio.tenpo.domain.ApiCallLogDTO.class));
    }

    @Test
    void testFilter_RequestWithError() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/test?param=value").build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        RuntimeException exception = new RuntimeException("Test error");
        when(webFilterChain.filter(exchange)).thenReturn(Mono.error(exception));
        when(apiCallLoggingService.saveCallHistory(any(com.desafio.tenpo.domain.ApiCallLogDTO.class)))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = apiCallLoggingFilter.filter(exchange, webFilterChain);

        // Assert
        StepVerifier.create(result).verifyErrorMatches(throwable -> throwable.getMessage().equals("Test error"));
        verify(apiCallLoggingService).saveCallHistory(argThat(logEntity -> {
            return logEntity.getError() != null && logEntity.getError().equals("Test error");
        }));
    }

    @Test
    void testFilter_ErrorSavingLog() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        response.setStatusCode(HttpStatus.OK);
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(webFilterChain.filter(exchange)).thenReturn(Mono.empty());
        when(apiCallLoggingService.saveCallHistory(any(com.desafio.tenpo.domain.ApiCallLogDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Void> result = apiCallLoggingFilter.filter(exchange, webFilterChain);

        // Assert
        StepVerifier.create(result).verifyComplete();
        verify(apiCallLoggingService).saveCallHistory(any(com.desafio.tenpo.domain.ApiCallLogDTO.class));
    }
}
