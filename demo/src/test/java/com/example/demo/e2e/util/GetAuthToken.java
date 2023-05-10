package com.example.demo.e2e.util;

import com.example.demo.security.jwt.dto.LoginRequest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class GetAuthToken {

    public static String getAuthTokenFromAdmin(){
        return
            given()
                .request()
                    .body(new LoginRequest("admin@gmail.com", "admin"))
                    .contentType(ContentType.JSON).
            when()
                .post("/api/auth/login").
            then()
                .extract().cookie("AuthToken");
    }

    public static String getAuthTokenFromUser(){
        return
            given()
                .request()
                    .body(new LoginRequest("user@gmail.com", "user"))
                    .contentType(ContentType.JSON).
            when()
                .post("/api/auth/login").
            then()
                .extract().cookie("AuthToken");
    }
}
