package com.desafio.tenpo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient mockWebClient() {
        ExchangeFunction mockExchangeFunction = clientRequest -> {
            // Create simulated response
            ClientResponse mockResponse = ClientResponse
                    .create(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body("10.0")
                    .build();

            return Mono.just(mockResponse);
        };

        return WebClient.builder()
                .exchangeFunction(mockExchangeFunction)
                .build();
    }
}
