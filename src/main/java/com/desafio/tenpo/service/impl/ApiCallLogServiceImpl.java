package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.exceptions.ExternalServiceException;
import com.desafio.tenpo.repository.ApiCallLogRepository;
import com.desafio.tenpo.service.ApiCallLoggingService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ApiCallLogServiceImpl implements ApiCallLoggingService {

    private final ApiCallLogRepository repository;
    private static final Logger log = LoggerFactory.getLogger(ApiCallLogServiceImpl.class);

    @Override
    public Mono<Void> saveCallHistory(ApiCallLogEntity apiCallLogEntity) {
        log.info("Attempting to save API call log: {}", apiCallLogEntity);
        return repository.save(apiCallLogEntity)
                .doOnSuccess(saved -> log.info("Successfully saved API call log with ID: {}", saved.getId()))
                .doOnError(error -> log.error("Error saving API call log", error))
                .then();
    }

    @Override
    public Flux<ApiCallLogEntity> getHistoricalApiCalls(Pageable pageable) {
        log.info("Retrieving API call logs - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findWithPagination(pageable)
                .onErrorMap(error -> {
                    log.error("Error retrieving API call logs", error);
                    return new ExternalServiceException("Failed to retrieve API call logs",
                            HttpStatus.SERVICE_UNAVAILABLE);
                });
    }
}
