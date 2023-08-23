package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GeneratorNotFoundException extends RuntimeException{

    public GeneratorNotFoundException() {
        super("Generator not found");
    }

    public GeneratorNotFoundException(String message) {
        super(message);
    }

    public GeneratorNotFoundException(long generatorId) {
        super("Generator " + generatorId + " not found");
    }
}