package com.example.demo.e2e;

import com.example.demo.model.Society;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.util.Locale;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class SocietyRestControllerE2ETest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        RestAssured.port = port;
    }

    @Test
    void whenGetNotExistingSocietyById_thenShouldGiveSocietyNotFoundError404() {
        when()
            .get("/api/societies/{id}", 1).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("Society 1 not found"))
                .body("uriRequested", equalTo("/api/societies/1"));
    }

    @Test
    void testGetSocietyById() {
        Society storedSociety = addSociety();

        when()
            .get("/api/societies/{id}", storedSociety.getId()).
        then()
            .assertThat()
                .statusCode(200)
                .body("cifDni", equalTo(storedSociety.getCifDni()))
                .body("name", equalTo(storedSociety.getName()));
    }

    private Society addSociety() {
        Society society = new Society("cifDni", "name");

        return
            given()
                .request()
                    .body(society)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/societies").
            then()
                .extract().as(Society.class);
    }

    @Test
    void whenAddInvalidSociety_thenShouldGiveBadRequestError400() {
        Society society = new Society(" ", "name");

        given()
            .request()
                .body(society)
                .contentType(ContentType.JSON).
        when()
            .post("/api/societies").
        then()
            .assertThat()
                .statusCode(400)
                .body("statusCode", equalTo(400))
                .body("message", equalTo("cifDni: must not be blank"))
                .body("uriRequested", equalTo("/api/societies"));

        notExistSociety(society.getId());
    }

    private void notExistSociety(long id) {
        when()
            .get("/api/societies/{id}", id).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("Society " + id + " not found"))
                .body("uriRequested", equalTo("/api/societies/" + id));
    }

    @Test
    void whenAddDuplicatedSociety_thenShouldGiveUnprocessableEntityError422() {
        Society storedSociety = addSociety();
        Society society = new Society(storedSociety.getCifDni(),storedSociety.getName());

        given()
            .request()
                .body(society)
                .contentType(ContentType.JSON).
        when()
            .post("/api/societies").
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", containsStringIgnoringCase("uc_society_cifdni"))
                .body("uriRequested", equalTo("/api/societies"));

        existSociety(storedSociety);
    }

    private void existSociety(Society society) {
        when()
            .get("/api/societies/{id}", society.getId()).
        then()
            .assertThat()
                .statusCode(200)
                .body("cifDni", equalTo(society.getCifDni()))
                .body("name", equalTo(society.getName()));
    }

    @Test
    void testAddSociety() {
        Society society = new Society("cifDni","name");

        Society addedSociety =
            given()
                .request()
                    .body(society)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/societies").
            then()
                .assertThat()
                    .statusCode(201)
                    .body("cifDni", equalTo(society.getCifDni()))
                    .body("name", equalTo(society.getName()))
                .extract().as(Society.class);

        //Esto esta bien para cuando es grande el modelo
        //society.setId(createdSociety.getId());
        //assertEquals(society, createdSociety);

        society.setId(addedSociety.getId());
        existSociety(society);
    }

    @Test
    void whenDeleteNotExistingSociety_thenShouldGiveSocietyNotFoundError404() {
        when()
            .delete("/api/societies/{id}", 1).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("Society 1 not found"))
                .body("uriRequested", equalTo("/api/societies/1"));
    }

    @Test
    void deleteSocietyTest() {
        Society storedSociety = addSociety();

        when()
            .delete("/api/societies/{id}", storedSociety.getId()).
        then()
            .assertThat()
                .statusCode(204);

        notExistSociety(storedSociety.getId());
    }
}
