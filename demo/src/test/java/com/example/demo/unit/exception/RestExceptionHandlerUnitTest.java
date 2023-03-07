package com.example.demo.unit.exception;

import com.example.demo.exception.ErrorInfo;
import com.example.demo.exception.RestExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerUnitTest {

    private RestExceptionHandler restExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        restExceptionHandler = new RestExceptionHandler();
        when(request.getRequestURI()).thenReturn("/test/uri");
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        String field = "fieldName";
        String errorMessage = "error message";

        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException methodArgumentNotValidException = new MethodArgumentNotValidException(parameter, bindingResult);
        FieldError fieldError = mock(FieldError.class);

        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
        when(fieldError.getField()).thenReturn(field);
        when(fieldError.getDefaultMessage()).thenReturn(errorMessage);


        ResponseEntity<ErrorInfo> response = restExceptionHandler.handleMethodArgumentNotValidException(request, methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("fieldName: error message", response.getBody().getMessage());
        assertEquals("/test/uri", response.getBody().getUriRequested());
    }

    @Test
    void testHandleDataIntegrityViolationException() {
        DataIntegrityViolationException dataIntegrityViolationException = mock(DataIntegrityViolationException.class);
        SQLException sqlException = mock(SQLException.class);

        when(dataIntegrityViolationException.getMostSpecificCause()).thenReturn(sqlException);
        when(sqlException.getMessage()).thenReturn("error message");

        ResponseEntity<ErrorInfo> response = restExceptionHandler.handleDataIntegrityViolationException(request, dataIntegrityViolationException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("error message", response.getBody().getMessage());
        assertEquals("/test/uri", response.getBody().getUriRequested());
    }
}