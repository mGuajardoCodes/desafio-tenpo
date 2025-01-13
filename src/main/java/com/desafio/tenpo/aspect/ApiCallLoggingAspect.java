package com.desafio.tenpo.aspect;

import com.desafio.tenpo.entity.ApiCallLogEntity;
import com.desafio.tenpo.service.impl.ApiCallLogServiceImpl;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@AllArgsConstructor
public class ApiCallLoggingAspect {

    private final ApiCallLogServiceImpl apiCallLogService;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logSuccess(Object result, ServerHttpRequest request) {
        ApiCallLogEntity log = new ApiCallLogEntity();
        log.setTimestamp(LocalDateTime.now());
        log.setEndpoint(request.getURI().getPath());
        log.setParameters(request.getURI().getQuery());
        log.setResponse(result != null ? result.toString() : "null");
        apiCallLogService.saveLogApiCall(log)
                .subscribe();
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    public void logError(Throwable exception, ServerHttpRequest request) {
        ApiCallLogEntity log = new ApiCallLogEntity();
        log.setTimestamp(LocalDateTime.now());
        log.setEndpoint(request.getURI().getPath());
        log.setParameters(request.getURI().getQuery());
        log.setError(exception.getMessage());
        apiCallLogService.saveLogApiCall(log)
                .subscribe();
    }
}
