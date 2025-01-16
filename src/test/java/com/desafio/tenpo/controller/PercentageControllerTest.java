package com.desafio.tenpo.controller;

import com.desafio.tenpo.domain.PercentageDTO;
import com.desafio.tenpo.exceptions.BadRequestException;
import com.desafio.tenpo.service.PercentageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.http.MediaType.APPLICATION_JSON;


@ExtendWith(MockitoExtension.class)
public class PercentageControllerTest {

    @Mock
    private PercentageService percentageService;

    @InjectMocks
    private PercentageController percentageController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Build the WebTestClient by binding to the specific controller instance
        webTestClient = WebTestClient.bindToController(percentageController).build();
        Mockito.when(percentageService.calculatePercentage(Mockito.any(Mono.class)))
                .thenReturn(Mono.just(15));
    }

    @Test
    @DisplayName("Should return a valid response when num1 and num2 are present")
    void testCalculatePercentageOk() {
        // Given a valid PercentageDTO
        PercentageDTO validRequest = new PercentageDTO();
        validRequest.setNum1(5);
        validRequest.setNum2(10);

        // Perform the POST request
        webTestClient.post()
                .uri("/api/percentage")
                .contentType(APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                // We expect 200 OK because both fields are set
                .expectStatus().isOk()
                // We expect an integer in the response body
                .expectBody(Integer.class)
                .isEqualTo(15);
    }

    @Test
    @DisplayName("Should throw BadRequestException when 'num1' is null")
    void testNullNum1ThrowsBadRequest() {
        // Create a request where num1 is null
        PercentageDTO request = new PercentageDTO();
        request.setNum1(null);
        request.setNum2(10);

        // Call the controller method directly and verify the exception
        StepVerifier.create(percentageController.calculatePercentage(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof BadRequestException
                                && throwable.getMessage().contains("Field 'num1' cannot be null")
                )
                .verify();
    }

    @Test
    @DisplayName("Should throw BadRequestException when 'num2' is null")
    void testNullNum2ThrowsBadRequest() {
        PercentageDTO request = new PercentageDTO();
        request.setNum1(10);
        request.setNum2(null);

        // Call the controller method directly and verify the exception
        StepVerifier.create(percentageController.calculatePercentage(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof BadRequestException
                                && throwable.getMessage().contains("Field 'num2' cannot be null")
                )
                .verify();
    }
}
