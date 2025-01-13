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

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ApiCallLogServiceImpl implements ApiCallLoggingService {

    private final ApiCallLogRepository repository;
    private static final Logger log = LoggerFactory.getLogger(ApiCallLogServiceImpl.class);

    /**
     * Saves an API call log to the database reactively.
     *
     * @param apiCallLogEntity The API call log entity to save.
     * @return A Mono that completes when the operation is done.
     */

    public Mono<Void> saveLogApiCall(ApiCallLogEntity apiCallLogEntity) {

        if (!isValidLog(apiCallLogEntity))
            log.warn("Invalid API call log entity. Skipping save operation.");

        return repository.save(apiCallLogEntity)
                .doOnSuccess(savedLog ->
                        log.info("API call log saved successfully. Endpoint: {}", savedLog.getEndpoint()))
                .doOnError(e -> log.error("Failed to log API call for endpoint {}: {}",
                        apiCallLogEntity.getEndpoint(), e.getMessage(), e)).then();
    }


    /**
     * Retrieves a paginated list of historical API call logs reactively.
     *
     * @param pageable Pageable object for pagination.
     * @return A Flux of API call log entities.
     */
    public Flux<ApiCallLogEntity> getHistoricalApiCalls(Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();
        return repository.findWithPagination(limit, offset);
    }


    /**
     * Validates the API call log entity.
     *
     * @param apiCallLogEntity The API call log entity to validate.
     * @return True if valid, false otherwise.
     */
    private boolean isValidLog(ApiCallLogEntity apiCallLogEntity) {
        return apiCallLogEntity != null &&
                Objects.nonNull(apiCallLogEntity.getEndpoint()) &&
                Objects.nonNull(apiCallLogEntity.getTimestamp()) &&
                apiCallLogEntity.getTimestamp().isBefore(LocalDateTime.now());
    }

}
