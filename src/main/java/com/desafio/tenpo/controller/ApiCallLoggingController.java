package com.desafio.tenpo.controller;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.service.ApiCallLoggingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiCallLoggingController {

    private static final Logger log = LoggerFactory.getLogger(ApiCallLoggingController.class);
    private ApiCallLoggingService apiCallLoggingService;

    @Operation(
            summary = "Retrieve API call logs",
            description = "Fetches historical API call logs with pagination support."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logs retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            // Bad practice ????? Expose entity ???? probably yes XD sorry is 4:30 am, im tired :(
                            schema = @Schema(implementation = ApiCallLogEntity.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "External server unavailable",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/logs")
    public Flux<ApiCallLogEntity> getApiCallLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Instant startTime = Instant.now();
        log.info("Get api call logs flow started");

        Pageable pageable = PageRequest.of(page, size);
        return apiCallLoggingService.getHistoricalApiCalls(pageable)
                .doFinally(signalType -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    log.info("Get api call logs flow finished in {} milliseconds", duration.toMillis());
                });
    }
}
