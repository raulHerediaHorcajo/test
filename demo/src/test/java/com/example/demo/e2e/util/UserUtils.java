package com.example.demo.e2e.util;

import com.example.demo.model.User;

import static com.example.demo.e2e.util.GetToken.getAuthTokenFromAdmin;
import static io.restassured.RestAssured.given;

public class UserUtils {

    public static User addUser(User user) {
        return
            given()
                .request()
                    .cookie("AuthToken", getAuthTokenFromAdmin())
                    .body(user)
                    .contentType("application/json")
            .when()
                .post("/api/users")
            .then()
                .extract().as(User.class);
    }
}
