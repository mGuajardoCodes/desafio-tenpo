package com.desafio.tenpo.repository;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ApiCallLogRepository extends R2dbcRepository<ApiCallLogEntity, Long> {

    /**
     * Query with manual pagination using LIMIT and OFFSET.
     *
     * @param limit Maximum number of records to return.
     * @param offset Offset from the start of the records.
     * @return A Flux containing the paginated records.
     */

    @Query("SELECT * FROM api_call_log_entity LIMIT :limit OFFSET :offset")
    Flux<ApiCallLogEntity> findWithPagination(int limit, int offset);
}
