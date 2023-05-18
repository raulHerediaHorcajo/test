package com.example.demo.unit.security.jwt.dto;

import com.example.demo.security.jwt.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LoginRequest model tests")
class LoginRequestUnitTest {

    private LoginRequest loginRequest;

    @BeforeEach
    public void setUp() {
        loginRequest = new LoginRequest(
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA=="
        );
    }

    @Test
    void testGetUsername() {
        assertEquals("test@gmail.com", loginRequest.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("ZXhhbXBsZSBwYXNzd29yZA==", loginRequest.getPassword());
    }

    @Test
    void testSetUsername() {
        loginRequest.setUsername("other@gmail.com");
        assertEquals("other@gmail.com", loginRequest.getUsername());
    }

    @Test
    void testSetPassword() {
        loginRequest.setPassword("yYyYyYyYyYyYyYyYy==");
        assertEquals("yYyYyYyYyYyYyYyYy==", loginRequest.getPassword());
    }
}