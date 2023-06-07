package com.example.demo.security.jwt.component;

import com.example.demo.security.jwt.util.SecurityCipher;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieManager {

	public static final String ACCESS_TOKEN_COOKIE_NAME = "AuthToken";
	public static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken";

	public HttpCookie createAccessTokenCookie(String token) {
		String encryptedToken = SecurityCipher.encrypt(token);
		return ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, encryptedToken).maxAge(-1).httpOnly(true).path("/").build();
	}

	public HttpCookie createRefreshTokenCookie(String token) {
		String encryptedToken = SecurityCipher.encrypt(token);
		return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, encryptedToken).maxAge(-1).httpOnly(true).path("/").build();
	}

	public HttpCookie deleteTokenCookie(Cookie cookie) {
		return ResponseCookie.from(cookie.getName(), "").maxAge(0).httpOnly(true).path("/").build();
	}
}
