package com.desafio.tenpo.service;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface ApiCallLoggingService {

    Flux<ApiCallLogEntity> getHistoricalApiCalls(Pageable pageable);
}
