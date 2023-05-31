package com.example.demo.unit.controller;

import com.example.demo.security.AuthRestController;
import com.example.demo.security.jwt.AuthService;
import com.example.demo.security.jwt.dto.AuthResponse;
import com.example.demo.security.jwt.dto.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerUnitTest {

    private AuthRestController authRestController;

    @Mock
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        authRestController = new AuthRestController(authService);
    }

    @Test
    void testLogin() {
        LoginRequest loginRequest = mock(LoginRequest.class);
        String accessToken = mock(Cookie.class).getValue();
        String refreshToken = mock(Cookie.class).getValue();
        HttpHeaders headers = new HttpHeaders();
        AuthResponse authResponse = mock(AuthResponse.class);
        ResponseEntity<AuthResponse> loginResponse = ResponseEntity.ok().headers(headers).body(authResponse);
        when(authService.login(loginRequest, accessToken, refreshToken)).thenReturn(loginResponse);

        ResponseEntity<AuthResponse> result = authRestController.login(accessToken, refreshToken, loginRequest);

        verify(authService).login(loginRequest, accessToken, refreshToken);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders()).isEqualTo(headers);
        assertThat(result.getBody()).isEqualTo(authResponse);
    }

    @Test
    void testRefreshToken() {
        String refreshToken = mock(Cookie.class).getValue();
        HttpHeaders headers = new HttpHeaders();
        AuthResponse authResponse = mock(AuthResponse.class);
        ResponseEntity<AuthResponse> refreshTokenResponse = ResponseEntity.ok().headers(headers).body(authResponse);
        when(authService.refresh(refreshToken)).thenReturn(refreshTokenResponse);

        ResponseEntity<AuthResponse> result = authRestController.refreshToken(refreshToken);

        verify(authService).refresh(refreshToken);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders()).isEqualTo(headers);
        assertThat(result.getBody()).isEqualTo(authResponse);
    }

    @Test
    void testLogout() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpHeaders headers = new HttpHeaders();
        AuthResponse authResponse = mock(AuthResponse.class);
        ResponseEntity<AuthResponse> logoutResponse = ResponseEntity.ok().headers(headers).body(authResponse);
        when(authService.logout(request)).thenReturn(logoutResponse);

        ResponseEntity<AuthResponse> result = authRestController.logout(request);

        verify(authService).logout(request);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders()).isEqualTo(headers);
        assertThat(result.getBody()).isEqualTo(authResponse);
    }
}
