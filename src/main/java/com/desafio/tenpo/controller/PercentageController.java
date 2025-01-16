package com.desafio.tenpo.controller;

import com.desafio.tenpo.domain.PercentageDTO;
import com.desafio.tenpo.exceptions.BadRequestException;
import com.desafio.tenpo.service.PercentageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@RestController
@RequestMapping("/api/percentage")
@AllArgsConstructor
public class PercentageController {

    private static final Logger log = LoggerFactory.getLogger(PercentageController.class);
    private PercentageService percentageService;


    @Operation(
            summary = "Calculate percentage",
            description = "Calculates the percentage based on the provided numbers."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Percentage successfully calculated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public Mono<Integer> calculatePercentage(@RequestBody PercentageDTO request) {
        Instant startTime = Instant.now();
        log.info("Calculate percentage flow started");

        return validateRequest(request)
                .then(percentageService.calculatePercentage(Mono.just(request)))
                .doFinally(signalType -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    log.info("Calculate percentage finished in {} milliseconds", duration.toMillis());
                });
    }

    private Mono<Void> validateRequest(PercentageDTO request) {
        return Mono.just(request)
                .filter(req -> Objects.nonNull(req.getNum1()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadRequestException("Field 'num1' cannot be null"))))
                .filter(req -> Objects.nonNull(req.getNum2()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadRequestException("Field 'num2' cannot be null"))))
                .then();
    }
}
