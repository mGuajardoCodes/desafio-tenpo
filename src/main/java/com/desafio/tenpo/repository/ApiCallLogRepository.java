package com.desafio.tenpo.repository;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ApiCallLogRepository extends ReactiveCrudRepository<ApiCallLogEntity, Long> {

    @Query("SELECT * FROM api_call_log_entity LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}")
    Flux<ApiCallLogEntity> findWithPagination(Pageable pageable);
}
