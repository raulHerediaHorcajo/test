package com.example.demo.e2e.generatortype;

import com.example.demo.model.GeneratorType;
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
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:InitializationTestData.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class GeneratorTypeRestControllerE2ETest {

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
    void testGetGeneratorTypes(String scenario, Map<String, String> params, List<GeneratorType> expectedGeneratorTypes) {
        addGeneratorType(new GeneratorType("name1"));
        addGeneratorType(new GeneratorType("name2"));
        addGeneratorType(new GeneratorType("name3"));

        List<GeneratorType> resultedGeneratorTypes = given()
            .request()
                .cookie("AuthToken", authToken)
                .params(params).
        when()
            .get("/api/generator-types").
        then()
            .assertThat()
                .statusCode(200)
                .body("numberOfElements", equalTo(expectedGeneratorTypes.size()))
                .extract().jsonPath().getList("content", GeneratorType.class);

        assertThat(resultedGeneratorTypes).containsAll(expectedGeneratorTypes);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("Get GeneratorTypes without filters",
                new HashMap<>(),
                List.of(
                    new GeneratorType(1, "name1"),
                    new GeneratorType(2, "name2"),
                    new GeneratorType(3, "name3")
                )
            ),
            arguments("Get GeneratorTypes with filters",
                new HashMap<String, String>() {{
                    put("name", "name1");
                }},
                List.of(
                    new GeneratorType(1, "name1")
                )
            ),
            arguments("Get GeneratorTypes with unmatched filters",
                new HashMap<String, String>() {{
                    put("name", "name4");
                }},
                new ArrayList<>()
            )
        );
    }

    @Test
    void whenGetNotExistGeneratorTypeById_thenShouldGiveGeneratorTypeNotFoundError404() {
        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/generator-types/{id}", 1).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("GeneratorType 1 not found"))
                .body("uriRequested", equalTo("/api/generator-types/1"));
    }

    @Test
    void testGetGeneratorTypeById() {
        GeneratorType storedGeneratorType = addGeneratorType();

        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/generator-types/{id}", storedGeneratorType.getId()).
        then()
            .assertThat()
                .statusCode(200)
                .body("id", equalTo((int)storedGeneratorType.getId()))
                .body("name", equalTo(storedGeneratorType.getName()));
    }

    private GeneratorType addGeneratorType() {
        return addGeneratorType(new GeneratorType("name"));
    }

    private GeneratorType addGeneratorType(GeneratorType generatorType) {
        return
            given()
                .request()
                    .cookie("AuthToken", authToken)
                    .body(generatorType)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/generator-types").
            then()
                .extract().as(GeneratorType.class);
    }

    @Test
    void whenAddInvalidGeneratorType_thenShouldGiveBadRequestError400() {
        GeneratorType newGeneratorType = new GeneratorType(" ");

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGeneratorType)
                .contentType(ContentType.JSON).
        when()
            .post("/api/generator-types").
        then()
            .assertThat()
                .statusCode(400)
                .body("statusCode", equalTo(400))
                .body("message", equalTo("name: must not be blank"))
                .body("uriRequested", equalTo("/api/generator-types"));
    }

    @Test
    void whenAddDuplicatedGeneratorType_thenShouldGiveUnprocessableEntityError422() {
        GeneratorType storedGeneratorType = addGeneratorType();
        GeneratorType newGeneratorType = new GeneratorType(storedGeneratorType.getName());

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGeneratorType)
                .contentType(ContentType.JSON).
        when()
            .post("/api/generator-types").
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", equalTo("The object of entity generatortype cannot be created or updated with the duplicate attribute name"))
                .body("uriRequested", equalTo("/api/generator-types"));

        existGeneratorType(storedGeneratorType);
    }

    private void existGeneratorType(GeneratorType generatorType) {
        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/generator-types/{id}", generatorType.getId()).
        then()
            .assertThat()
                .statusCode(200)
                .body("id", equalTo((int)generatorType.getId()))
                .body("name", equalTo(generatorType.getName()));
    }

    @Test
    void testAddGeneratorType() {
        GeneratorType newGeneratorType = new GeneratorType("name");

        GeneratorType addedGeneratorType =
            given()
                .request()
                    .cookie("AuthToken", authToken)
                    .body(newGeneratorType)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/generator-types").
            then()
                .assertThat()
                    .statusCode(201)
                    .body("id", equalTo(1))
                    .body("name", equalTo(newGeneratorType.getName()))
                .extract().as(GeneratorType.class);

        newGeneratorType.setId(addedGeneratorType.getId());
        existGeneratorType(newGeneratorType);
    }

    @Test
    void whenUpdateNotExistGeneratorType_thenShouldGiveGeneratorTypeNotFoundError404() {
        GeneratorType newGeneratorType = new GeneratorType("name");

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGeneratorType)
                .contentType(ContentType.JSON).
        when()
            .put("/api/generator-types/{id}", 1).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("GeneratorType 1 not found"))
                .body("uriRequested", equalTo("/api/generator-types/1"));
    }

    @Test
    void whenUpdateInvalidGeneratorType_thenShouldGiveBadRequestError400() {
        GeneratorType storedGeneratorType = addGeneratorType();
        GeneratorType newGeneratorType = new GeneratorType(" ");

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGeneratorType)
                .contentType(ContentType.JSON).
        when()
            .put("/api/generator-types/{id}", storedGeneratorType.getId()).
        then()
            .assertThat()
                .statusCode(400)
                .body("statusCode", equalTo(400))
                .body("message", equalTo("name: must not be blank"))
                .body("uriRequested", equalTo("/api/generator-types/" + storedGeneratorType.getId()));

        existGeneratorType(storedGeneratorType);
    }

    @Test
    void whenUpdateDuplicatedGeneratorType_thenShouldGiveUnprocessableEntityError422() {
        GeneratorType storedGeneratorType1 = addGeneratorType();
        GeneratorType storedGeneratorType2 = addGeneratorType(new GeneratorType("otherName"));
        GeneratorType newGeneratorType = new GeneratorType(storedGeneratorType2.getName());

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGeneratorType)
                .contentType(ContentType.JSON).
        when()
            .put("/api/generator-types/{id}", storedGeneratorType1.getId()).
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", equalTo("The object of entity generatortype cannot be created or updated with the duplicate attribute name"))
                .body("uriRequested", equalTo("/api/generator-types/" + storedGeneratorType1.getId()));

        existGeneratorType(storedGeneratorType1);
        existGeneratorType(storedGeneratorType2);
    }

    @Test
    void testUpdateGeneratorType() {
        GeneratorType storedGeneratorType = addGeneratorType();
        GeneratorType newGeneratorType = new GeneratorType("otherName");

        GeneratorType updatedGeneratorType = given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGeneratorType)
                .contentType(ContentType.JSON).
        when()
            .put("/api/generator-types/{id}", storedGeneratorType.getId()).
        then()
            .assertThat()
                .statusCode(200)
                .body("id", equalTo((int)storedGeneratorType.getId()))
                .body("name", equalTo(newGeneratorType.getName()))
            .extract().as(GeneratorType.class);

        newGeneratorType.setId(updatedGeneratorType.getId());
        existGeneratorType(newGeneratorType);
    }

    @Test
    void whenDeleteNotExistGeneratorType_thenShouldGiveGeneratorTypeNotFoundError404() {
        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .delete("/api/generator-types/{id}", 1).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("GeneratorType 1 not found"))
                .body("uriRequested", equalTo("/api/generator-types/1"));
    }

    @Test
    void testDeleteGeneratorType() {
        GeneratorType storedGeneratorType = addGeneratorType();

        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .delete("/api/generator-types/{id}", storedGeneratorType.getId()).
        then()
            .assertThat()
                .statusCode(204);

        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/generator-types/{id}", storedGeneratorType.getId()).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("GeneratorType " + storedGeneratorType.getId() + " not found"))
                .body("uriRequested", equalTo("/api/generator-types/" + storedGeneratorType.getId()));
    }
}
