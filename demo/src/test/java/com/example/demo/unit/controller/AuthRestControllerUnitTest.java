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
        LoginRequest loginRequest = new LoginRequest("admin@gmail.com", "admin");
        String accessToken = mock(Cookie.class).getValue();
        String refreshToken = mock(Cookie.class).getValue();
        HttpHeaders headers = new HttpHeaders();
        AuthResponse authResponse = new AuthResponse(
            AuthResponse.Status.SUCCESS,
            "Auth successful. Tokens are created in cookie."
        );
        ResponseEntity<AuthResponse> loginResponse = ResponseEntity.ok().headers(headers).body(authResponse);
        when(authService.login(loginRequest, accessToken, refreshToken)).thenReturn(loginResponse);

        ResponseEntity<AuthResponse> result = authRestController.login(accessToken, refreshToken, loginRequest);

        verify(authService).login(loginRequest, accessToken, refreshToken);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders()).isEqualTo(headers);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        assertThat(result.getBody().getMessage()).isEqualTo("Auth successful. Tokens are created in cookie.");
    }

    @Test
    void testRefreshToken() {
        String refreshToken = mock(Cookie.class).getValue();
        HttpHeaders headers = new HttpHeaders();
        AuthResponse authResponse = new AuthResponse(
            AuthResponse.Status.SUCCESS,
            "Auth successful. Token is created in cookie."
        );
        ResponseEntity<AuthResponse> refreshTokenResponse = ResponseEntity.ok().headers(headers).body(authResponse);
        when(authService.refresh(refreshToken)).thenReturn(refreshTokenResponse);

        ResponseEntity<AuthResponse> result = authRestController.refreshToken(refreshToken);

        verify(authService).refresh(refreshToken);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders()).isEqualTo(headers);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        assertThat(result.getBody().getMessage()).isEqualTo("Auth successful. Token is created in cookie.");
    }

    @Test
    void testLogout() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpHeaders headers = new HttpHeaders();
        AuthResponse authResponse = new AuthResponse(
            AuthResponse.Status.SUCCESS,
            "logout successfully"
        );
        ResponseEntity<AuthResponse> logoutResponse = ResponseEntity.ok().headers(headers).body(authResponse);
        when(authService.logout(request)).thenReturn(logoutResponse);

        ResponseEntity<AuthResponse> result = authRestController.logout(request);

        verify(authService).logout(request);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders()).isEqualTo(headers);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        assertThat(result.getBody().getMessage()).isEqualTo("logout successfully");
    }
}
