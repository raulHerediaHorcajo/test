package com.example.demo.unit.security.jwt.dto;

import com.example.demo.security.jwt.dto.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Token model tests")
class TokenUnitTest {

    private Token token;

    @BeforeEach
    public void setUp() {
        token = new Token(
            Token.TokenType.ACCESS,
            "eyXXXXXXXXXXXXXX",
            1684434545163L,
            LocalDateTime.of(2023, 5, 18, 10, 30, 0)
        );
    }

    @Test
    void testGetTokenType() {
        assertEquals(Token.TokenType.ACCESS, token.getTokenType());
    }

    @Test
    void testGetTokenValue() {
        assertEquals("eyXXXXXXXXXXXXXX", token.getTokenValue());
    }

    @Test
    void testGetDuration() {
        assertEquals(1684434545163L, token.getDuration());
    }

    @Test
    void testGetExpiryDate() {
        assertEquals(LocalDateTime.of(2023, 5, 18, 10, 30, 0), token.getExpiryDate());
    }

    @Test
    void testSetTokenType() {
        token.setTokenType(Token.TokenType.REFRESH);
        assertEquals(Token.TokenType.REFRESH, token.getTokenType());
    }

    @Test
    void testSetTokenValue() {
        token.setTokenValue("eyYYYYYYYYYYYYYY");
        assertEquals("eyYYYYYYYYYYYYYY", token.getTokenValue());
    }

    @Test
    void testSetDuration() {
        token.setDuration(0L);
        assertEquals(0L, token.getDuration());
    }

    @Test
    void testSetExpiryDate() {
        token.setExpiryDate(LocalDateTime.of(2024, 1, 11, 10, 30, 0));
        assertEquals(LocalDateTime.of(2024, 1, 11, 10, 30, 0), token.getExpiryDate());
    }
}