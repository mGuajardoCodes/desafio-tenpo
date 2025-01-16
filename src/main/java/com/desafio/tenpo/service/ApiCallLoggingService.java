package com.desafio.tenpo.service;

import com.desafio.tenpo.domain.ApiCallLogDTO;
import com.desafio.tenpo.entity.ApiCallLogEntity;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApiCallLoggingService {

    Mono<Void> saveCallHistory(ApiCallLogDTO apiCallLogDTO);

    Flux<ApiCallLogEntity> getHistoricalApiCalls(Pageable pageable);
}
