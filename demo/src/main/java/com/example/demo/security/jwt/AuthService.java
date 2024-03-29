package com.example.demo.security.jwt;

import com.example.demo.security.jwt.component.JwtCookieManager;
import com.example.demo.security.jwt.component.JwtTokenProvider;
import com.example.demo.security.jwt.dto.AuthResponse;
import com.example.demo.security.jwt.dto.LoginRequest;
import com.example.demo.security.jwt.dto.Token;
import com.example.demo.security.jwt.util.SecurityCipher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieManager cookieUtil;

    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtTokenProvider jwtTokenProvider,
            JwtCookieManager cookieUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieUtil = cookieUtil;

    }

    public ResponseEntity<AuthResponse> login(
            LoginRequest loginRequest,
            String encryptedAccessToken,
            String encryptedRefreshToken
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = SecurityCipher.decrypt(encryptedAccessToken);
        String refreshToken = SecurityCipher.decrypt(encryptedRefreshToken);

        String username = loginRequest.getUsername();
        UserDetails user = userDetailsService.loadUserByUsername(username);

        boolean accessTokenValid = jwtTokenProvider.validateToken(accessToken);
        boolean refreshTokenValid = jwtTokenProvider.validateToken(refreshToken);

        HttpHeaders responseHeaders = new HttpHeaders();
        Token newAccessToken;
        Token newRefreshToken;

        if (!accessTokenValid && refreshTokenValid) {
            newAccessToken = jwtTokenProvider.generateToken(user);
            addAccessTokenCookie(responseHeaders, newAccessToken);
        } else {
            newAccessToken = jwtTokenProvider.generateToken(user);
            newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }

        AuthResponse loginResponse = new AuthResponse(
                AuthResponse.Status.SUCCESS,
                "Auth successful. Tokens are created in cookie."
        );

        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }

    public ResponseEntity<AuthResponse> refresh(String encryptedRefreshToken) {
        String refreshToken = SecurityCipher.decrypt(encryptedRefreshToken);

        boolean refreshTokenValid = jwtTokenProvider.validateToken(refreshToken);

        if (!refreshTokenValid) {
            AuthResponse loginResponse = new AuthResponse(
                    AuthResponse.Status.FAILURE,
                    "Invalid refresh token!"
            );

            return ResponseEntity.ok().body(loginResponse);
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);

        Token newAccessToken = jwtTokenProvider.generateToken(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        addAccessTokenCookie(responseHeaders, newAccessToken);

        AuthResponse refreshResponse = new AuthResponse(
                AuthResponse.Status.SUCCESS,
                "Auth successful. Token is created in cookie."
        );

        return ResponseEntity.ok().headers(responseHeaders).body(refreshResponse);
    }

    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                deleteTokenCookie(responseHeaders, cookie);
            }
        }

        AuthResponse logoutResponse = new AuthResponse(
            AuthResponse.Status.SUCCESS,
            "logout successfully"
        );
        return ResponseEntity.ok().headers(responseHeaders).body(logoutResponse);
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createAccessTokenCookie(token.getTokenValue()).toString());
    }

    private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createRefreshTokenCookie(token.getTokenValue()).toString());
    }

    private void deleteTokenCookie(HttpHeaders httpHeaders, Cookie cookie) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
            cookieUtil.deleteTokenCookie(cookie).toString());
    }
}
