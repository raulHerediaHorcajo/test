package com.example.demo.unit.exception;

import com.example.demo.exception.ErrorInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorInfoUnitTest {

    private ErrorInfo errorInfo;

    @BeforeEach
    public void setUp() {
        errorInfo = new ErrorInfo(HttpStatus.BAD_REQUEST.value(), "Test message", "/test/uri");
    }

    @Test
    void testGetStatusCode() {
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorInfo.getStatusCode());
    }

    @Test
    void testGetMessage() {
        assertEquals("Test message", errorInfo.getMessage());
    }

    @Test
    void testGetUriRequested() {
        assertEquals("/test/uri", errorInfo.getUriRequested());
    }
}
