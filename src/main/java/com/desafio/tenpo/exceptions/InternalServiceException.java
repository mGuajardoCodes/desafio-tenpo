package com.desafio.tenpo.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServiceException extends RuntimeException {

    private final HttpStatus status;

    public InternalServiceException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
