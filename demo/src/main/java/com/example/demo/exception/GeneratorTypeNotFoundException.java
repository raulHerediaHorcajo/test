package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GeneratorTypeNotFoundException extends RuntimeException{

    public GeneratorTypeNotFoundException() {
        super("GeneratorType not found");
    }

    public GeneratorTypeNotFoundException(String name) {
        super("GeneratorType " + name + " not found");
    }

    public GeneratorTypeNotFoundException(long generatorTypeId) {
        super("GeneratorType " + generatorTypeId + " not found");
    }
}
