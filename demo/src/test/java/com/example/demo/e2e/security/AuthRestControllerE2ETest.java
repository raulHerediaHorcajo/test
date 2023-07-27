package com.example.demo.e2e.security;

import com.example.demo.e2e.util.GetToken;
import com.example.demo.security.jwt.dto.AuthResponse;
import com.example.demo.security.jwt.dto.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:InitializationTestData.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class AuthRestControllerE2ETest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void whenLoginWithInvalidLoginRequest_thenShouldGiveUnauthorizedError401(
        String scenario, LoginRequest loginRequest) {
        given()
            .request()
                .body(loginRequest)
                .contentType(ContentType.JSON).
        when()
            .post("/api/auth/login").
        then()
            .assertThat()
                .statusCode(401)
                .header("Set-Cookie", equalTo(null));
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("LoginRequest with Valid Email and Invalid Password",
                new LoginRequest("admin@gmail.com", "invalid")
            ),
            arguments("LoginRequest with Invalid Email and Password",
                new LoginRequest("invalid@gmail.com", "invalid")
            )
        );
    }

    @Test
    void testLogin() {
        given()
            .request()
                .body(new LoginRequest("admin@gmail.com", "admin"))
                .contentType(ContentType.JSON).
        when()
            .post("/api/auth/login").
        then()
            .assertThat()
                .statusCode(200)
                .cookie("AuthToken")
                .cookie("RefreshToken")
                .body("status", equalTo(AuthResponse.Status.SUCCESS.name()))
                .body("message", equalTo("Auth successful. Tokens are created in cookie."));
    }

    @Test
    void whenRefreshTokenWithInvalidToken_thenShouldGiveFailureStatusWithEmptyCookies() {
        given()
            .request()
                .cookie("RefreshToken", "invalidToken")
                .contentType(ContentType.JSON).
        when()
            .post("/api/auth/refresh").
        then()
            .assertThat()
                .statusCode(200)
                .header("Set-Cookie", equalTo(null))
                .body("status", equalTo(AuthResponse.Status.FAILURE.name()))
                .body("message", equalTo("Invalid refresh token!"));
    }

    @Test
    void whenRefreshToken_thenShouldGiveSuccessStatusWithAuthToken() {
        given()
            .request()
                .cookie("RefreshToken", GetToken.getRefreshTokenFromAdmin())
                .contentType(ContentType.JSON).
        when()
            .post("/api/auth/refresh").
        then()
            .assertThat()
                .statusCode(200)
                .cookie("AuthToken")
                .body("status", equalTo(AuthResponse.Status.SUCCESS.name()))
                .body("message", equalTo("Auth successful. Token is created in cookie."));
    }

    @Test
    void whenLogoutWithoutTokens_thenShouldGiveSuccessStatusWithEmptyCookies() {
        given()
            .request()
                .contentType(ContentType.JSON).
        when()
            .post("/api/auth/logout").
        then()
            .assertThat()
                .statusCode(200)
                .header("Set-Cookie", equalTo(null))
                .body("status", equalTo(AuthResponse.Status.SUCCESS.name()))
                .body("message", equalTo("logout successfully"));
    }

    @Test
    void whenLogoutWithTokens_thenShouldGiveSuccessStatusWithBlankCookies() {
        given()
            .cookie("AuthToken", GetToken.getAuthTokenFromUser())
            .cookie("RefreshToken", GetToken.getRefreshTokenFromUser())
            .contentType(ContentType.JSON).
        when()
            .post("/api/auth/logout").
        then()
            .assertThat()
                .statusCode(200)
                .cookie("AuthToken", "")
                .cookie("RefreshToken", "")
                .body("status", equalTo(AuthResponse.Status.SUCCESS.name()))
                .body("message", equalTo("logout successfully"));
    }
}
