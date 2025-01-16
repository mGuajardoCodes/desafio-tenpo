package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.adapter.ApiCallLogAdapter;
import com.desafio.tenpo.domain.ApiCallLogDTO;
import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.exceptions.ExternalServiceException;
import com.desafio.tenpo.repository.ApiCallLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApiCallLogServiceImplTest {

    @Mock
    private ApiCallLogRepository repository;

    @InjectMocks
    private ApiCallLogServiceImpl service;

    @Mock
    ApiCallLogAdapter apiCallLogAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should save API call log successfully")
    void saveCallHistorySuccess() {
        ApiCallLogDTO log = new ApiCallLogDTO(1L,
                java.time.LocalDateTime.now(), "/api/test",
                "param=value", "200 OK", null);

        ApiCallLogEntity apiCallLogEntity = new ApiCallLogEntity(1L,
                java.time.LocalDateTime.now(), "/api/test",
                "param=value", "200 OK", null);

        when(apiCallLogAdapter.toEntity(any())).thenReturn(apiCallLogEntity);
        when(repository.save(any(ApiCallLogEntity.class))).thenReturn(Mono.just(apiCallLogEntity));

        Mono<Void> result = service.saveCallHistory(log);

        StepVerifier.create(result)
                .verifyComplete();
        verify(repository, times(1)).save(apiCallLogEntity);
    }

    @Test
    @DisplayName("Should handle error when retrieving API call logs")
    void getHistoricalApiCallsError() {

        when(repository.findWithPagination(any(Pageable.class)))
                .thenReturn(Flux.error(new RuntimeException("Database down")));

        Flux<ApiCallLogEntity> result = service.getHistoricalApiCalls(PageRequest.of(0, 10));

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ExternalServiceException &&
                                throwable.getMessage().equals("Failed to retrieve API call logs"))
                .verify();
        verify(repository, times(1)).findWithPagination(any(Pageable.class));
    }
}
