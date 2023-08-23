package com.example.demo.e2e.util;

import com.example.demo.model.Society;
import io.restassured.http.ContentType;

import static com.example.demo.e2e.util.GetToken.getAuthTokenFromAdmin;
import static io.restassured.RestAssured.given;

public class SocietyUtils {

    public static Society addSociety() {
        return addSociety(new Society("XXXXXXXXXX", "Test Society"));
    }

    public static Society addSociety(Society society) {
        return
            given()
                .request()
                    .cookie("AuthToken", getAuthTokenFromAdmin())
                    .body(society)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/societies").
            then()
                .extract().as(Society.class);
    }
}