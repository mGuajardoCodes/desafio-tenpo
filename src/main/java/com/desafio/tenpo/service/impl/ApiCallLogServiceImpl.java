package com.desafio.tenpo.service.impl;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.repository.ApiCallLogRepository;
import com.desafio.tenpo.service.ApiCallLoggingService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
        return repository.save(apiCallLogEntity).then();
    }

    @Override
    public Flux<ApiCallLogEntity> getHistoricalApiCalls(Pageable pageable) {
        return repository.findWithPagination(pageable);
    }
}
