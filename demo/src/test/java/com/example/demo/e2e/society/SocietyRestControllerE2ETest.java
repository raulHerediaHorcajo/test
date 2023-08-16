package com.example.demo.e2e.society;

import com.example.demo.model.Society;
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

import java.util.*;
import java.util.stream.Stream;

import static com.example.demo.e2e.util.GetToken.getAuthTokenFromAdmin;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:InitializationTestData.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class SocietyRestControllerE2ETest {

    private static String authToken;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        RestAssured.port = port;

        authToken = getAuthTokenFromAdmin();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void testGetSocieties(String scenario, Map<String, String> params, List<Society> expectedSocieties) {
        addSociety(new Society("XXXXXXXXXX", "Test Society 1"));
        addSociety(new Society("YYYYYYYYYY", "Test Society 2"));
        addSociety(new Society("ZZZZZZZZZZ", "Test Society 3"));

        List<Society> resultedSocieties = given()
            .request()
                .cookie("AuthToken", authToken)
                .params(params).
        when()
            .get("/api/societies").
        then()
            .assertThat()
                .statusCode(200)
                .body("numberOfElements", equalTo(expectedSocieties.size()))
                .extract().jsonPath().getList("content", Society.class);

        assertThat(resultedSocieties).containsAll(expectedSocieties);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("Get societies without filters",
                new HashMap<>(),
                List.of(
                    new Society(1, "XXXXXXXXXX", "Test Society 1"),
                    new Society(2, "YYYYYYYYYY", "Test Society 2"),
                    new Society(3, "ZZZZZZZZZZ", "Test Society 3")
                )
            ),
            arguments("Get societies with filters",
                new HashMap<String, String>() {{
                    put("cifDni", "XXXXXXXXXX");
                    put("name", "Test Society 1");
                }},
                List.of(
                    new Society(1, "XXXXXXXXXX", "Test Society 1")
                )
            ),
            arguments("Get societies with unmatched filters",
                new HashMap<String, String>() {{
                    put("cifDni", "XXXXXXXXXX");
                    put("name", "Test Society 2");
                }},
                new ArrayList<>()
            )
        );
    }

    @Test
    void whenGetNotExistSocietyById_thenShouldGiveSocietyNotFoundError404() {
        given()
            .request()
                .cookie("AuthToken", authToken).
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

        given()
            .request()
                .cookie("AuthToken", authToken).
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
        return addSociety(new Society("XXXXXXXXXX", "Test Society"));
    }

    private Society addSociety(Society society) {
        return
            given()
                .request()
                    .cookie("AuthToken", authToken)
                    .body(society)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/societies").
            then()
                .extract().as(Society.class);
    }

    @Test
    void whenAddInvalidSociety_thenShouldGiveBadRequestError400() {
        Society newSociety = new Society(" ", "Test Society");

        given()
            .request()
                .cookie("AuthToken", authToken)
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
                .cookie("AuthToken", authToken)
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
        given()
            .request()
                .cookie("AuthToken", authToken).
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
        Society newSociety = new Society("XXXXXXXXXX","Test Society");

        Society addedSociety =
            given()
                .request()
                    .cookie("AuthToken", authToken)
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

        //Esto está bien para cuando es grande el modelo
        //society.setId(createdSociety.getId());
        //assertEquals(society, createdSociety);

        newSociety.setId(addedSociety.getId());
        existSociety(newSociety);
    }

    @Test
    void whenUpdateNotExistSociety_thenShouldGiveSocietyNotFoundError404() {
        Society newSociety = new Society("otherCifDni","otherName");

        given()
            .request()
                .cookie("AuthToken", authToken)
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
        Society newSociety = new Society(" ", "otherName");

        given()
            .request()
                .cookie("AuthToken", authToken)
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
                .cookie("AuthToken", authToken)
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

        Society updatedSociety = given()
            .request()
                .cookie("AuthToken", authToken)
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
        given()
            .request()
                .cookie("AuthToken", authToken).
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

        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .delete("/api/societies/{id}", storedSociety.getId()).
        then()
            .assertThat()
                .statusCode(204);

        given()
            .request()
                .cookie("AuthToken", authToken).
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
