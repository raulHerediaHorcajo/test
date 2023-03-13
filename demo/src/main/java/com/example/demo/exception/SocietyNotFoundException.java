package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SocietyNotFoundException extends RuntimeException{

    public SocietyNotFoundException() {
        super("Society not found");
    }

    public SocietyNotFoundException(String message) {
        super(message);
    }

    public SocietyNotFoundException(long societyId) {
        super("Society " + societyId + " not found");
    }
}
