package com.desafio.tenpo.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class WebClientConfigTest {

    private final WebClientConfiguration webClientConfiguration = new WebClientConfiguration();

    @Test
    void testMockWebClient_ResponseOk() {
        WebClient webClient = webClientConfiguration.mockWebClient();

        Mono<ClientResponse> responseMono = webClient.get().uri("/test").exchangeToMono(Mono::just);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    // Verify status code
                    assert response.statusCode() == HttpStatus.OK;

                    // Verify headers
                    HttpHeaders headers = response.headers().asHttpHeaders();
                    assert headers.getContentType() != null;
                    assert headers.getContentType().toString().equals("application/json");

                    // Verify body
                    StepVerifier.create(response.bodyToMono(String.class))
                            .expectNext("10.0")
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void testMockWebClient_ExchangeFunctionInvoked() {
        WebClient webClient = webClientConfiguration.mockWebClient();

        Mono<String> bodyMono = webClient.get().uri("/test")
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(bodyMono)
                .expectNext("10.0")
                .verifyComplete();
    }
}
