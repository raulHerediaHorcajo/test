package com.example.demo.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        StringBuilder errorMessage = new StringBuilder();
        fieldErrors.forEach(f -> errorMessage.append(f.getField()).append(": ").append(f.getDefaultMessage()).append(", "));
        errorMessage.delete(errorMessage.length()-2, errorMessage.length());

        ErrorInfo errorInfo = new ErrorInfo(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), request.getRequestURI());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorInfo> handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {
        ErrorInfo errorInfo;

        if (e.getCause() instanceof UnrecognizedPropertyException urpe) {
            errorInfo = new ErrorInfo(HttpStatus.BAD_REQUEST.value(), "Unrecognized property: " + urpe.getPropertyName(), request.getRequestURI());
        } else if (e.getCause() instanceof JsonProcessingException jpe) {
            errorInfo = new ErrorInfo(HttpStatus.BAD_REQUEST.value(), jpe.getMessage(), request.getRequestURI());
        } else {
            errorInfo = new ErrorInfo(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
        }

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        String errorMessage = e.getMostSpecificCause().getMessage();

        final String uniquePattern = "(?i)^.*uc_(\\w+)_(\\w+).*$";
        final String fkPattern = "(?i)^.*fk_(\\w+)_on_(\\w+).*$";
        if (errorMessage.matches(uniquePattern)) {
            String entity = errorMessage.replaceAll(uniquePattern, "$1");
            String attribute = errorMessage.replaceAll(uniquePattern, "$2");
            errorMessage = "The object of entity " + entity + " cannot be created or updated with the duplicate attribute " + attribute;
        } else if (errorMessage.matches(fkPattern)) {
            String entity1 = errorMessage.replaceAll(fkPattern, "$1");
            String entity2 = errorMessage.replaceAll(fkPattern, "$2");
            errorMessage = "The object of entity " + entity1 + " cannot be created or updated if it is related to the non-existing " +
                "entity " + entity2 + ", or the entity " + entity2 + " cannot be deleted if it is related to entity " + entity1;
        }

        ErrorInfo errorInfo = new ErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY.value(), errorMessage, request.getRequestURI());
        return new ResponseEntity<>(errorInfo, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(SocietyNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleSocietyNotFoundException(HttpServletRequest request, SocietyNotFoundException e) {
        ErrorInfo errorInfo = new ErrorInfo(HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleUserNotFoundException(HttpServletRequest request, UserNotFoundException e) {
        ErrorInfo errorInfo = new ErrorInfo(HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GeneratorTypeNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleGeneratorTypeNotFoundException(HttpServletRequest request, GeneratorTypeNotFoundException e) {
        ErrorInfo errorInfo = new ErrorInfo(HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }
}
