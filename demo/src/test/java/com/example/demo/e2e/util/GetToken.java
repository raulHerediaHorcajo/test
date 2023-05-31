package com.example.demo.e2e.util;

import com.example.demo.security.jwt.dto.LoginRequest;
import io.restassured.http.ContentType;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetToken {

    public static Map<String, String> getTokens(LoginRequest loginRequest){
        return
            given()
                .request()
                    .body(loginRequest)
                    .contentType(ContentType.JSON).
                when()
                    .post("/api/auth/login").
                then()
                    .extract().cookies();
    }

    public static String getAuthTokenFromAdmin(){
        return
            getTokens(
                new LoginRequest(
                "admin@gmail.com",
                "admin")
            ).get("AuthToken");
    }

    public static String getAuthTokenFromUser(){
        return
            getTokens(
                new LoginRequest(
                    "user@gmail.com",
                    "user")
            ).get("AuthToken");
    }

    public static String getRefreshTokenFromAdmin(){
        return
            getTokens(
                new LoginRequest(
                    "admin@gmail.com",
                    "admin")
            ).get("RefreshToken");
    }

    public static String getRefreshTokenFromUser(){
        return
            getTokens(
                new LoginRequest(
                    "user@gmail.com",
                    "user")
            ).get("RefreshToken");
    }
}
