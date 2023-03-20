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
    void whenGetNotExistSocietyById_thenShouldGiveSocietyNotFoundError404() {
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
                .body("id", equalTo((int)storedSociety.getId()))
                .body("cifDni", equalTo(storedSociety.getCifDni()))
                .body("name", equalTo(storedSociety.getName()));
    }

    private Society addSociety() {
        return addSociety(new Society("cifDni", "name"));
    }

    private Society addSociety(Society society) {
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
        Society newSociety = new Society(" ", "name");

        given()
            .request()
                .body(newSociety)
                .contentType(ContentType.JSON).
        when()
            .post("/api/societies").
        then()
            .assertThat()
                .statusCode(400)
                .body("statusCode", equalTo(400))
                .body("message", equalTo("cifDni: must not be blank"))
                .body("uriRequested", equalTo("/api/societies"));
    }

    @Test
    void whenAddDuplicatedSociety_thenShouldGiveUnprocessableEntityError422() {
        Society storedSociety = addSociety();
        Society newSociety = new Society(storedSociety.getCifDni(),storedSociety.getName());

        given()
            .request()
                .body(newSociety)
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
                .body("id", equalTo((int)society.getId()))
                .body("cifDni", equalTo(society.getCifDni()))
                .body("name", equalTo(society.getName()));
    }

    @Test
    void testAddSociety() {
        Society newSociety = new Society("cifDni","name");

        Society addedSociety =
            given()
                .request()
                    .body(newSociety)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/societies").
            then()
                .assertThat()
                    .statusCode(201)
                    .body("id", equalTo(1))
                    .body("cifDni", equalTo(newSociety.getCifDni()))
                    .body("name", equalTo(newSociety.getName()))
                .extract().as(Society.class);

        //Esto esta bien para cuando es grande el modelo
        //society.setId(createdSociety.getId());
        //assertEquals(society, createdSociety);

        newSociety.setId(addedSociety.getId());
        existSociety(newSociety);
    }

    @Test
    void whenUpdateNotExistSociety_thenShouldGiveSocietyNotFoundError404() {
        Society newSociety = new Society("cifDni","name");

        given()
            .request()
                .body(newSociety)
                .contentType(ContentType.JSON).
        when()
            .put("/api/societies/{id}", 1).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("Society 1 not found"))
                .body("uriRequested", equalTo("/api/societies/1"));
    }

    @Test
    void whenUpdateInvalidSociety_thenShouldGiveBadRequestError400() {
        Society storedSociety = addSociety();
        Society newSociety = new Society(" ", "name");

        given()
            .request()
                .body(newSociety)
                .contentType(ContentType.JSON).
        when()
            .put("/api/societies/{id}", storedSociety.getId()).
        then()
            .assertThat()
                .statusCode(400)
                .body("statusCode", equalTo(400))
                .body("message", equalTo("cifDni: must not be blank"))
                .body("uriRequested", equalTo("/api/societies/" + storedSociety.getId()));

        existSociety(storedSociety);
    }

    @Test
    void whenUpdateDuplicatedSociety_thenShouldGiveUnprocessableEntityError422() {
        Society storedSociety1 = addSociety();
        Society storedSociety2 = addSociety(new Society("otherCifDni", "otherName"));
        Society newSociety = new Society(storedSociety2.getCifDni(),storedSociety2.getName());

        given()
            .request()
                .body(newSociety)
                .contentType(ContentType.JSON).
        when()
            .put("/api/societies/{id}", storedSociety1.getId()).
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", containsStringIgnoringCase("uc_society_cifdni"))
                .body("uriRequested", equalTo("/api/societies/" + storedSociety1.getId()));

        existSociety(storedSociety1);
        existSociety(storedSociety2);
    }

    @Test
    void testUpdateSociety() {
        Society storedSociety = addSociety();
        Society newSociety = new Society("otherCifDni", "otherName");

        Society updatedSociety =given()
            .request()
                .body(newSociety)
                .contentType(ContentType.JSON).
        when()
            .put("/api/societies/{id}", storedSociety.getId()).
        then()
            .assertThat()
                .statusCode(200)
                .body("id", equalTo((int)storedSociety.getId()))
                .body("cifDni", equalTo(newSociety.getCifDni()))
                .body("name", equalTo(newSociety.getName()))
            .extract().as(Society.class);

        newSociety.setId(updatedSociety.getId());
        existSociety(newSociety);
    }

    @Test
    void whenDeleteNotExistSociety_thenShouldGiveSocietyNotFoundError404() {
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
    void testDeleteSociety() {
        Society storedSociety = addSociety();

        when()
            .delete("/api/societies/{id}", storedSociety.getId()).
        then()
            .assertThat()
                .statusCode(204);

        when()
            .get("/api/societies/{id}", storedSociety.getId()).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("Society " + storedSociety.getId() + " not found"))
                .body("uriRequested", equalTo("/api/societies/" + storedSociety.getId()));
    }
}