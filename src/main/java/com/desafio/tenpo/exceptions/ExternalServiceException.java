package com.desafio.tenpo.exceptions;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExternalServiceException extends RuntimeException {

    private final HttpStatus status;

    @Builder
    public ExternalServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
