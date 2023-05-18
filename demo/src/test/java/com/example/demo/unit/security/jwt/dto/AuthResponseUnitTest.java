package com.example.demo.unit.security.jwt.dto;

import com.example.demo.security.jwt.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AuthResponse model tests")
class AuthResponseUnitTest {

    private AuthResponse authResponse;

    @BeforeEach
    public void setUp() {
        authResponse = new AuthResponse(
            AuthResponse.Status.SUCCESS,
            "Auth successful",
            "Test error message"
        );
    }

    @Test
    void testGetStatus() {
        assertEquals(AuthResponse.Status.SUCCESS, authResponse.getStatus());
    }

    @Test
    void testGetMessage() {
        assertEquals("Auth successful", authResponse.getMessage());
    }

    @Test
    void testGetError() {
        assertEquals("Test error message", authResponse.getError());
    }

    @Test
    void testSetStatus() {
        authResponse.setStatus(AuthResponse.Status.FAILURE);
        assertEquals(AuthResponse.Status.FAILURE, authResponse.getStatus());
    }

    @Test
    void testSetMessage() {
        authResponse.setMessage("Auth failed");
        assertEquals("Auth failed", authResponse.getMessage());
    }

    @Test
    void testSetError() {
        authResponse.setError("New test error message");
        assertEquals("New test error message", authResponse.getError());
    }
}