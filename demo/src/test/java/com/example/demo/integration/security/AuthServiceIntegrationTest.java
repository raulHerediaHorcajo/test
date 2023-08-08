package com.example.demo.integration.security;

import com.example.demo.model.User;
import com.example.demo.security.config.SecurityExpressions.UserRole;
import com.example.demo.security.jwt.AuthService;
import com.example.demo.security.jwt.component.JwtTokenProvider;
import com.example.demo.security.jwt.dto.AuthResponse;
import com.example.demo.security.jwt.dto.LoginRequest;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;

    @SpyBean
    private PasswordEncoder passwordEncoder;
    @SpyBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void testLogin() {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        when(passwordEncoder.matches("example password", "ZXhhbXBsZSBwYXNzd29yZA==")).thenReturn(true);
        User storedUser = userService.addUser(
            new User(
                "Test User",
                "test@gmail.com",
                "example password",
                List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );

        LoginRequest loginRequest = new LoginRequest(storedUser.getEmail(), "example password");
        String encryptedAccessToken = "encryptedAccessToken";
        String encryptedRefreshToken = "encryptedRefreshToken";

        ResponseEntity<AuthResponse> result = authService.login(loginRequest,
            encryptedAccessToken,
            encryptedRefreshToken);

        assertThat(result).isNotNull();
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders().get(HttpHeaders.SET_COOKIE))
            .isNotNull()
            .hasSize(2)
            .elements(0, 1).asString().contains("AuthToken=", "RefreshToken=");
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        assertThat(result.getBody().getMessage()).isEqualTo("Auth successful. Tokens are created in cookie.");
    }

    @Test
    void testRefresh() {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        when(passwordEncoder.matches("example password", "ZXhhbXBsZSBwYXNzd29yZA==")).thenReturn(true);
        User storedUser = userService.addUser(
            new User(
                "Test User 1",
                "test1@gmail.com",
                "example password",
                List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );

        String encryptedRefreshToken = "encryptedRefreshToken";
        when(jwtTokenProvider.validateToken(null)).thenReturn(true);
        doReturn(storedUser.getEmail()).when(jwtTokenProvider).getUsername(null);

        ResponseEntity<AuthResponse> result = authService.refresh(encryptedRefreshToken);

        assertThat(result).isNotNull();
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders().get(HttpHeaders.SET_COOKIE))
            .isNotNull()
            .hasSize(1)
            .element(0).asString().contains("AuthToken=");
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        assertThat(result.getBody().getMessage()).isEqualTo("Auth successful. Token is created in cookie.");
    }

    @Test
    void testLogout() {
        ResponseEntity<AuthResponse> result = authService.logout(request);

        assertThat(result).isNotNull();
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders().get(HttpHeaders.SET_COOKIE))
            .isNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        assertThat(result.getBody().getMessage()).isEqualTo("logout successfully");
    }
}