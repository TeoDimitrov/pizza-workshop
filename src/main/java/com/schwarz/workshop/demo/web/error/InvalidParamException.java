package com.schwarz.workshop.demo.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidParamException extends ResponseStatusException {
    public InvalidParamException(HttpStatus status) {
        super(status);
    }

    public InvalidParamException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
