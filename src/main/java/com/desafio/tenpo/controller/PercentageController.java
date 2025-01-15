package com.desafio.tenpo.controller;

import com.desafio.tenpo.domain.PercentageDTO;
import com.desafio.tenpo.exceptions.BadRequestException;
import com.desafio.tenpo.service.PercentageService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/percentage")
@AllArgsConstructor
public class PercentageController {

    private static final Logger log = LoggerFactory.getLogger(PercentageController.class);
    private PercentageService percentageService;

    @PostMapping
    public Mono<Integer> calculatePercentage(@RequestBody PercentageDTO request) {
        Instant startTime = Instant.now();
        log.info("Calculate percentage flow started");

        return percentageService.calculatePercentage(Mono.just(request)).doFinally(r -> {
            Duration duration = Duration.between(startTime, Instant.now());
            log.info("Calculate percentage finished in {} milliseconds", duration.toMillis());
        });
    }

}
