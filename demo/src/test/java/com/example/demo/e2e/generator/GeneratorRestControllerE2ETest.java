package com.example.demo.e2e.generator;

import com.example.demo.model.Generator;
import com.example.demo.model.GeneratorType;
import com.example.demo.model.Society;
import com.example.demo.util.builder.GeneratorBuilder;
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

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static com.example.demo.e2e.util.GeneratorTypeUtils.addGeneratorType;
import static com.example.demo.e2e.util.GeneratorUtils.addGenerator;
import static com.example.demo.e2e.util.GetToken.getAuthTokenFromAdmin;
import static com.example.demo.e2e.util.SocietyUtils.addSociety;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:InitializationTestData.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class GeneratorRestControllerE2ETest {

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
    void testGetGenerators(String scenario, Map<String, String> params, List<Generator> expectedGenerators) {
        addSociety();
        addGeneratorType();
        Society storedSociety2 = addSociety(new Society("YYYYYYYYYY", "Test Society 2"));
        addGenerator(new GeneratorBuilder().withName("Test Generator 1")
            .withSociety(storedSociety2).withActive(false)
            .withInitializationDate(LocalDate.of(2023, 6, 1))
            .build());
        addGenerator(new GeneratorBuilder().withName("Test Generator 2")
            .withAddress("C. Tulipán 2").build());
        addGenerator(new GeneratorBuilder().withName("Test Generator 3")
            .withAddress("C. Tulipán 3").build());

        List<Generator> resultedGenerators = given()
            .request()
                .cookie("AuthToken", authToken)
                .params(params).
        when()
            .get("/api/generators").
        then()
            .assertThat()
                .statusCode(200)
                .body("numberOfElements", equalTo(expectedGenerators.size()))
                .extract().jsonPath().getList("content", Generator.class);

        assertThat(resultedGenerators).containsAll(expectedGenerators);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("Get generators without filters",
                new HashMap<>(),
                List.of(
                    new GeneratorBuilder().withId(1).withName("Test Generator 1")
                        .withSociety(new Society(2, "YYYYYYYYYY", "Test Society 2")).withActive(false)
                        .withInitializationDate(LocalDate.of(2023, 6, 1))
                        .build(),
                    new GeneratorBuilder().withId(2).withName("Test Generator 2")
                        .withAddress("C. Tulipán 2").build(),
                    new GeneratorBuilder().withId(3).withName("Test Generator 3")
                        .withAddress("C. Tulipán 3").build()
                )
            ),
            arguments("Get generators with filters",
                new HashMap<String, Object>() {{
                    put("name", "Test Generator 1");
                    put("societyCifDni", "YYYYYYYYYY");
                    put("societyName", "Test Society 2");
                    put("active", false);
                    put("startInitializationDate", "2023-06-01");
                }},
                List.of(
                    new GeneratorBuilder().withId(1).withName("Test Generator 1")
                        .withSociety(new Society(2, "YYYYYYYYYY", "Test Society 2")).withActive(false)
                        .withInitializationDate(LocalDate.of(2023, 6, 1))
                        .build()
                )
            ),
            arguments("Get generators with unmatched filters",
                new HashMap<String, Object>() {{
                    put("name", "Test Generator 2");
                    put("societyCifDni", "YYYYYYYYYY");
                    put("active", false);
                    put("phoneNumber", "111111111");
                    put("address", "C. Tulipán 3");
                }},
                new ArrayList<>()
            )
        );
    }

    @Test
    void whenGetNotExistGeneratorById_thenShouldGiveGeneratorNotFoundError404() {
        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/generators/{id}", 1).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("Generator 1 not found"))
                .body("uriRequested", equalTo("/api/generators/1"));
    }

    @Test
    void testGetGeneratorById() {
        addSociety();
        addGeneratorType();
        Generator storedGenerator = addGenerator();

        Generator resultGenerator =
            given()
                .request()
                    .cookie("AuthToken", authToken).
            when()
                .get("/api/generators/{id}", storedGenerator.getId()).
            then()
                .assertThat()
                    .statusCode(200)
                    .body("id", equalTo((int)storedGenerator.getId()))
                    .body("name", equalTo(storedGenerator.getName()))
                .extract().as(Generator.class);

        assertThat(resultGenerator).isEqualTo(storedGenerator);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("addGeneratorButNotExistRelationScenarios")
    void whenAddGeneratorButNotExistRelation_thenShouldGiveUnprocessableEntityError422(String scenario, Generator newGenerator, String expectedMessage) {
        addSociety();
        addGeneratorType();

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGenerator)
                .contentType(ContentType.JSON).
        when()
            .post("/api/generators").
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", equalTo(expectedMessage))
                .body("uriRequested", equalTo("/api/generators"));
    }

    private static Stream<Arguments> addGeneratorButNotExistRelationScenarios() {
        return Stream.of(
            arguments("Society Relation",
                new GeneratorBuilder()
                    .withSociety(new Society(2,"YYYYYYYYYY","Test Society 2")).build(),
                "The object of entity generator cannot be created if it is related to the non-existing " +
                "entity society, or the object entity society cannot be deleted if it is related to entity generator"
            ),
            arguments("GeneratorType Relation",
                new GeneratorBuilder().withName("otherName")
                    .withGeneratorType(new GeneratorType(2, "Test GeneratorType 2")).build(),
                "The object of entity generator cannot be created if it is related to the non-existing " +
                "entity generatortype, or the object entity generatortype cannot be deleted if it is related to entity generator"
            )
        );
    }


    @Test
    void whenAddInvalidGenerator_thenShouldGiveBadRequestError400() {
        Generator newGenerator = new GeneratorBuilder().withName(" ").build();

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGenerator)
                .contentType(ContentType.JSON).
        when()
            .post("/api/generators").
        then()
            .assertThat()
                .statusCode(400)
                .body("statusCode", equalTo(400))
                .body("message", equalTo("name: must not be blank"))
                .body("uriRequested", equalTo("/api/generators"));
    }

    @Test
    void whenAddDuplicatedGenerator_thenShouldGiveUnprocessableEntityError422() {
        addSociety();
        addGeneratorType();
        Generator storedGenerator = addGenerator();
        Generator newGenerator = new GeneratorBuilder().build();

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGenerator)
                .contentType(ContentType.JSON).
        when()
            .post("/api/generators").
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", equalTo("The object of entity generator cannot be created or updated with the duplicate attribute name"))
                .body("uriRequested", equalTo("/api/generators"));

        existGenerator(storedGenerator);
    }

    private void existGenerator(Generator generator) {
        Generator resultGenerator =
            given()
                .request()
                    .cookie("AuthToken", authToken).
            when()
                .get("/api/generators/{id}", generator.getId()).
            then()
                .assertThat()
                    .statusCode(200)
                    .body("id", equalTo((int)generator.getId()))
                    .body("name", equalTo(generator.getName()))
                .extract().as(Generator.class);

        assertThat(resultGenerator).isEqualTo(generator);
    }

    @Test
    void testAddGenerator() {
        addSociety();
        addGeneratorType();
        Generator newGenerator = new GeneratorBuilder().build();

        Generator addedGenerator =
            given()
                .request()
                    .cookie("AuthToken", authToken)
                    .body(newGenerator)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/generators").
            then()
                .assertThat()
                    .statusCode(201)
                    .body("id", equalTo(1))
                    .body("name", equalTo(newGenerator.getName()))
                .extract().as(Generator.class);

        //Esto está bien para cuando es grande el modelo
        newGenerator.setId(addedGenerator.getId());
        assertThat(addedGenerator).isEqualTo(newGenerator);

        existGenerator(newGenerator);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("updateGeneratorButNotExistRelationScenarios")
    void whenUpdateGeneratorButNotExistRelation_thenShouldGiveUnprocessableEntityError422(String scenario, Generator newGenerator, String expectedMessage) {
        addSociety();
        addGeneratorType();
        addGenerator();

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGenerator)
                .contentType(ContentType.JSON).
        when()
            .put("/api/generators/{id}", 1).
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", equalTo(expectedMessage))
                .body("uriRequested", equalTo("/api/generators/1"));
    }

    private static Stream<Arguments> updateGeneratorButNotExistRelationScenarios() {
        return Stream.of(
            arguments("Society Relation",
                new GeneratorBuilder().withName("otherName")
                    .withSociety(new Society(2,"YYYYYYYYYY","Test Society 2")).build(),
                "Unable to find com.example.demo.model.Society with id 2"
            ),
            arguments("GeneratorType Relation",
                new GeneratorBuilder().withName("otherName")
                    .withGeneratorType(new GeneratorType(2, "Test GeneratorType 2")).build(),
                "Unable to find com.example.demo.model.GeneratorType with id 2"
            )
        );
    }

    @Test
    void whenUpdateNotExistGenerator_thenShouldGiveGeneratorNotFoundError404() {
        Generator newGenerator = new GeneratorBuilder().withName("otherName").build();

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGenerator)
                .contentType(ContentType.JSON).
        when()
            .put("/api/generators/{id}", 1).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("Generator 1 not found"))
                .body("uriRequested", equalTo("/api/generators/1"));
    }

    @Test
    void whenUpdateInvalidGenerator_thenShouldGiveBadRequestError400() {
        addSociety();
        addGeneratorType();
        Generator storedGenerator = addGenerator();
        Generator newGenerator = new GeneratorBuilder().withName(" ").build();

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGenerator)
                .contentType(ContentType.JSON).
        when()
            .put("/api/generators/{id}", storedGenerator.getId()).
        then()
            .assertThat()
                .statusCode(400)
                .body("statusCode", equalTo(400))
                .body("message", equalTo("name: must not be blank"))
                .body("uriRequested", equalTo("/api/generators/" + storedGenerator.getId()));

        existGenerator(storedGenerator);
    }

    @Test
    void whenUpdateDuplicatedGenerator_thenShouldGiveUnprocessableEntityError422() {
        addSociety();
        addGeneratorType();
        Generator storedGenerator1 = addGenerator();
        Generator storedGenerator2 = addGenerator(new GeneratorBuilder().withName("otherName").withAddress("C. Tulipán 2").build());
        Generator newGenerator = new GeneratorBuilder().withName("otherName").withAddress("C. Tulipán 2").build();

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newGenerator)
                .contentType(ContentType.JSON).
        when()
            .put("/api/generators/{id}", storedGenerator1.getId()).
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", equalTo("The object of entity generator cannot be created or updated with the duplicate attribute name"))
                .body("uriRequested", equalTo("/api/generators/" + storedGenerator1.getId()));

        existGenerator(storedGenerator1);
        existGenerator(storedGenerator2);
    }

    @Test
    void testUpdateGenerator() {
        addSociety();
        addGeneratorType();
        Generator storedGenerator = addGenerator();
        Generator newGenerator = new GeneratorBuilder().withName("otherName").build();

        Generator updatedGenerator =
            given()
                .request()
                    .cookie("AuthToken", authToken)
                    .body(newGenerator)
                    .contentType(ContentType.JSON).
            when()
                .put("/api/generators/{id}", storedGenerator.getId()).
            then()
                .assertThat()
                    .statusCode(200)
                    .body("id", equalTo((int)storedGenerator.getId()))
                    .body("name", equalTo(newGenerator.getName()))
                .extract().as(Generator.class);

        newGenerator.setId(updatedGenerator.getId());
        existGenerator(newGenerator);
    }

    @Test
    void whenDeleteNotExistGenerator_thenShouldGiveGeneratorNotFoundError404() {
        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .delete("/api/generators/{id}", 1).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("Generator 1 not found"))
                .body("uriRequested", equalTo("/api/generators/1"));
    }

    @Test
    void testDeleteGenerator() {
        addSociety();
        addGeneratorType();
        Generator storedGenerator = addGenerator();

        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .delete("/api/generators/{id}", storedGenerator.getId()).
        then()
            .assertThat()
                .statusCode(204);

        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/generators/{id}", storedGenerator.getId()).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("Generator " + storedGenerator.getId() + " not found"))
                .body("uriRequested", equalTo("/api/generators/" + storedGenerator.getId()));
    }
}