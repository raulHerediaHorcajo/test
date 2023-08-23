package com.example.demo.e2e.util;

import com.example.demo.model.GeneratorType;
import io.restassured.http.ContentType;

import static com.example.demo.e2e.util.GetToken.getAuthTokenFromAdmin;
import static io.restassured.RestAssured.given;

public class GeneratorTypeUtils {

    public static GeneratorType addGeneratorType() {
        return addGeneratorType(new GeneratorType("Test GeneratorType"));
    }

    public static GeneratorType addGeneratorType(GeneratorType generatorType) {
        return
            given()
                .request()
                    .cookie("AuthToken", getAuthTokenFromAdmin())
                    .body(generatorType)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/generator-types").
            then()
                .extract().as(GeneratorType.class);
    }
}