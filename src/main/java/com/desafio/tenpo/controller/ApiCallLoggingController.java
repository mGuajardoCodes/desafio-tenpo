package com.desafio.tenpo.controller;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.service.ApiCallLoggingService;
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

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiCallLoggingController {

    private static final Logger log = LoggerFactory.getLogger(ApiCallLoggingController.class);
    private ApiCallLoggingService apiCallLoggingService;

    @GetMapping("/logs")
    public Flux<ApiCallLogEntity> getApiCallLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return apiCallLoggingService.getHistoricalApiCalls(pageable);
    }
}
