package com.example.demo.unit.exception;

import com.example.demo.exception.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("handleHttpMessageNotReadableException")
    void testHandleHttpMessageNotReadableException(String scenario, String errorMessage, List<Integer> expectedTimes) {
        UnrecognizedPropertyException unrecognizedPropertyException = mock(UnrecognizedPropertyException.class);
        JsonProcessingException jsonProcessingException = mock(JsonProcessingException.class);
        HttpMessageNotReadableException httpMessageNotReadableException = mock(HttpMessageNotReadableException.class);

        lenient().when(unrecognizedPropertyException.getPropertyName()).thenReturn("error property");
        lenient().when(jsonProcessingException.getMessage()).thenReturn("error message caused by JsonProcessingException");
        lenient().when(httpMessageNotReadableException.getMessage()).thenReturn("error message caused by OtherException");

        switch (scenario) {
            case "UnrecognizedPropertyException" -> when(httpMessageNotReadableException.getCause()).thenReturn(unrecognizedPropertyException);
            case "JsonProcessingException" -> when(httpMessageNotReadableException.getCause()).thenReturn(jsonProcessingException);
            default -> when(httpMessageNotReadableException.getCause()).thenReturn(httpMessageNotReadableException);
        }

        ResponseEntity<ErrorInfo> response = restExceptionHandler.handleHttpMessageNotReadableException(request, httpMessageNotReadableException);

        verify(request).getRequestURI();
        verify(unrecognizedPropertyException, times(expectedTimes.get(0))).getPropertyName();
        verify(jsonProcessingException, times(expectedTimes.get(1))).getMessage();
        verify(httpMessageNotReadableException, times(expectedTimes.get(2))).getMessage();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals("/test/uri", response.getBody().getUriRequested());
    }
    private static Stream<Arguments> handleHttpMessageNotReadableException() {
        return Stream.of(
            arguments("UnrecognizedPropertyException",
                "Unrecognized property: error property",
                List.of(1, 0, 0)
            ),
            arguments("JsonProcessingException",
                "error message caused by JsonProcessingException",
                List.of(0, 1, 0)
            ),
            arguments("OtherException",
                "error message caused by OtherException",
                List.of(0, 0, 1)
            )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("handleDataIntegrityViolationException")
    void testHandleDataIntegrityViolationException(String scenario, String errorMessage, String expectedErrorMessage) {
        DataIntegrityViolationException dataIntegrityViolationException = mock(DataIntegrityViolationException.class);
        SQLException sqlException = mock(SQLException.class);

        when(dataIntegrityViolationException.getMostSpecificCause()).thenReturn(sqlException);
        when(sqlException.getMessage()).thenReturn(errorMessage);

        ResponseEntity<ErrorInfo> response = restExceptionHandler.handleDataIntegrityViolationException(request, dataIntegrityViolationException);

        verify(request).getRequestURI();
        verify(dataIntegrityViolationException).getMostSpecificCause();
        verify(sqlException).getMessage();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody().getMessage());
        assertEquals("/test/uri", response.getBody().getUriRequested());
    }
    private static Stream<Arguments> handleDataIntegrityViolationException() {
        return Stream.of(
            arguments("Unique error",
                "uc_Entity_Attribute",
                "The object of entity Entity cannot be created or updated with the duplicate attribute Attribute"
            ),
            arguments("FK error",
                "fk_Entity1_on_Entity2",
                "The object of entity Entity1 cannot be created or updated if it is related to the non-existing " +
                    "entity Entity2, or the object entity Entity2 cannot be deleted if it is related to entity Entity1"
            ),
            arguments("Other error",
                "error message",
                "error message"
            )
        );
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

    @Test
    void testHandleGeneratorTypeNotFoundException() {
        GeneratorTypeNotFoundException generatorTypeNotFoundException = mock(GeneratorTypeNotFoundException.class);

        when(generatorTypeNotFoundException.getMessage()).thenReturn("error message");

        ResponseEntity<ErrorInfo> response = restExceptionHandler.handleGeneratorTypeNotFoundException(request, generatorTypeNotFoundException);

        verify(request).getRequestURI();
        verify(generatorTypeNotFoundException).getMessage();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error message", response.getBody().getMessage());
        assertEquals("/test/uri", response.getBody().getUriRequested());
    }
}