package com.desafio.tenpo.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.StandardException;

@StandardException
@Getter
@Setter
public class InternalServiceException extends RuntimeException {
    private String message;
}
