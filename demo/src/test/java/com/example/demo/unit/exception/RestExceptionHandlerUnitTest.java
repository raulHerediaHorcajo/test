package com.example.demo.unit.exception;

import com.example.demo.exception.ErrorInfo;
import com.example.demo.exception.RestExceptionHandler;
import com.example.demo.exception.SocietyNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RestExceptionHandlerUnitTest {

    @Autowired
    private RestExceptionHandler restExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        when(request.getRequestURI()).thenReturn("/test/uri");
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        List<String> fields = List.of("fieldName1", "fieldName2");
        List<String> errorMessages = List.of("error message1", "error message2");

        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException methodArgumentNotValidException = new MethodArgumentNotValidException(parameter, bindingResult);
        FieldError fieldError = mock(FieldError.class);

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError, fieldError));
        when(fieldError.getField()).thenReturn(fields.get(0), fields.get(1));
        when(fieldError.getDefaultMessage()).thenReturn(errorMessages.get(0), errorMessages.get(1));


        ResponseEntity<ErrorInfo> response = restExceptionHandler.handleMethodArgumentNotValidException(request, methodArgumentNotValidException);

        verify(request).getRequestURI();
        verify(bindingResult).getFieldErrors();
        verify(fieldError, times(2)).getField();
        verify(fieldError, times(2)).getDefaultMessage();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("fieldName1: error message1, fieldName2: error message2", response.getBody().getMessage());
        assertEquals("/test/uri", response.getBody().getUriRequested());
    }

    @Test
    void testHandleDataIntegrityViolationException() {
        DataIntegrityViolationException dataIntegrityViolationException = mock(DataIntegrityViolationException.class);
        SQLException sqlException = mock(SQLException.class);

        when(dataIntegrityViolationException.getMostSpecificCause()).thenReturn(sqlException);
        when(sqlException.getMessage()).thenReturn("error message");

        ResponseEntity<ErrorInfo> response = restExceptionHandler.handleDataIntegrityViolationException(request, dataIntegrityViolationException);

        verify(request).getRequestURI();
        verify(dataIntegrityViolationException).getMostSpecificCause();
        verify(sqlException).getMessage();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("error message", response.getBody().getMessage());
        assertEquals("/test/uri", response.getBody().getUriRequested());
    }

    @Test
    void testHandleSocietyNotFoundException() {
        SocietyNotFoundException societyNotFoundException = mock(SocietyNotFoundException.class);

        when(societyNotFoundException.getMessage()).thenReturn("error message");

        ResponseEntity<ErrorInfo> response = restExceptionHandler.handleSocietyNotFoundException(request, societyNotFoundException);

        verify(request).getRequestURI();
        verify(societyNotFoundException).getMessage();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error message", response.getBody().getMessage());
        assertEquals("/test/uri", response.getBody().getUriRequested());
    }

    @Test
    void testHandleUserNotFoundException() {
        UserNotFoundException userNotFoundException = mock(UserNotFoundException.class);

        when(userNotFoundException.getMessage()).thenReturn("error message");

        ResponseEntity<ErrorInfo> response = restExceptionHandler.handleUserNotFoundException(request, userNotFoundException);

        verify(request).getRequestURI();
        verify(userNotFoundException).getMessage();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error message", response.getBody().getMessage());
        assertEquals("/test/uri", response.getBody().getUriRequested());
    }
}