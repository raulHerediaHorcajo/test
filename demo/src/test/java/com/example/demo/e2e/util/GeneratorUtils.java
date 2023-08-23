package com.example.demo.e2e.util;

import com.example.demo.model.Generator;
import com.example.demo.util.builder.GeneratorBuilder;
import io.restassured.http.ContentType;

import static com.example.demo.e2e.util.GetToken.getAuthTokenFromAdmin;
import static io.restassured.RestAssured.given;

public class GeneratorUtils {

    public static Generator addGenerator() {
        return addGenerator(new GeneratorBuilder().build());
    }

    public static Generator addGenerator(Generator generator) {
        return
            given()
                .request()
                    .cookie("AuthToken", getAuthTokenFromAdmin())
                    .body(generator)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/generators").
            then()
                .extract().as(Generator.class);
    }
}