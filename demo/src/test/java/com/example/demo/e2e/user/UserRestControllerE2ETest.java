package com.example.demo.e2e.user;

import com.example.demo.model.User;
import com.example.demo.security.config.SecurityExpressions.UserRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;
import java.util.stream.Stream;

import static com.example.demo.e2e.util.GetToken.getAuthTokenFromAdmin;
import static com.example.demo.e2e.util.GetToken.getAuthTokenFromUser;
import static com.example.demo.e2e.util.UserUtils.addUser;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:InitializationTestData.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class UserRestControllerE2ETest {

    private static String authToken;
    private static String authUserToken;

    @SpyBean
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        RestAssured.port = port;

        authToken = getAuthTokenFromAdmin();
        authUserToken = getAuthTokenFromUser();
    }

    @Test
    void whenGetUsersWithUserRole_thenShouldGiveForbiddenError403() {
        given()
            .request()
                .cookie("AuthToken", authUserToken).
        when()
            .get("/api/users").
        then()
            .assertThat()
                .statusCode(403);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void testGetUsers(String scenario, Map<String, List<String>> params, List<User> expectedUsers) {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        addUser(new User(
            "Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name())
        ));
        addUser(new User(
            "Test User 4",
            "test4@gmail.com",
            "example password",
            List.of(UserRole.USER.name())
        ));
        addUser(new User(
            "Test User 5",
            "test5@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        ));

        List<User> resultedUsers = given()
            .request()
                .cookie("AuthToken", authToken)
                .params(params).
        when()
            .get("/api/users").
        then()
            .assertThat()
                .statusCode(200)
                .body("numberOfElements", equalTo(expectedUsers.size()))
                .extract().jsonPath().getList("content", User.class);

        assertThat(resultedUsers).containsAll(expectedUsers);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("Get users without filters",
                new HashMap<>(),
                List.of(
                    new User(1,
                        "admin",
                        "admin@gmail.com",
                        "$2a$10$gDc4SqW9Y9VsPNDV63krR.yNGhVkVBdRFUU9GUV6VhuSfi6neNr8K",
                        List.of(UserRole.ADMIN.name(), UserRole.USER.name())
                    ),
                    new User(2,
                        "user",
                        "user@gmail.com",
                        "$2a$10$1ok3CeCSVd/GyiguPQwAS.Nw3tvOoBcX0n4ZCn9wV5mpFy3Z74Z2.",
                        List.of(UserRole.USER.name())
                    ),
                    new User(3,
                        "Test User 3",
                        "test3@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.ADMIN.name())
                    ),
                    new User(4,
                        "Test User 4",
                        "test4@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.USER.name())
                    ),
                    new User(5,
                        "Test User 5",
                        "test5@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.ADMIN.name(), UserRole.USER.name())
                    )
                )
            ),
            arguments("Get users with filters",
                new HashMap<String, List<String>>() {{
                    put("name", List.of("Test User 3"));
                    put("email", List.of("test3@gmail.com"));
                    put("roles", List.of(UserRole.ADMIN.name()));
                }},
                List.of(
                    new User(3,
                        "Test User 3",
                        "test3@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.ADMIN.name())
                    )
                )
            ),
            arguments("Get users with unmatched filters",
                new HashMap<String, List<String>>() {{
                    put("name", List.of("Test User 3"));
                    put("email", List.of("test4@gmail.com"));
                    put("roles", List.of(UserRole.ADMIN.name(), UserRole.USER.name()));
                }},
                new ArrayList<>()
            )
        );
    }

    @Test
    void whenGetNotExistUserById_thenShouldGiveUserNotFoundError404() {
        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/users/{id}", 3).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("User 3 not found"))
                .body("uriRequested", equalTo("/api/users/3"));
    }

    @Test
    void whenGetAnotherUserByIdWithUserRole_thenShouldGiveForbiddenError403() {
        given()
            .request()
                .cookie("AuthToken", authUserToken).
        when()
            .get("/api/users/{id}", 1).
        then()
            .assertThat()
                .statusCode(403);
    }

    @Test
    void whenGetAnotherUserByIdWithAdminRole_thenShouldGiveAnotherUser() {
        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/users/{id}", 2).
        then()
            .assertThat()
                .statusCode(200)
                    .body("id", equalTo(2))
                    .body("name", equalTo("user"))
                    .body("email", equalTo("user@gmail.com"))
                    .body("password", equalTo("$2a$10$1ok3CeCSVd/GyiguPQwAS.Nw3tvOoBcX0n4ZCn9wV5mpFy3Z74Z2."))
                    .body("roles", equalTo(List.of(UserRole.USER.name())));
    }

    @Test
    void testGetUserById() {
        given()
            .request()
                .cookie("AuthToken", authUserToken).
        when()
            .get("/api/users/{id}", 2).
        then()
            .assertThat()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("name", equalTo("user"))
                .body("email", equalTo("user@gmail.com"))
                .body("password", equalTo("$2a$10$1ok3CeCSVd/GyiguPQwAS.Nw3tvOoBcX0n4ZCn9wV5mpFy3Z74Z2."))
                .body("roles", equalTo(List.of(UserRole.USER.name())));
    }

    @Test
    void whenAddInvalidUser_thenShouldGiveBadRequestError400() {
        User newUser = new User(" ",
            "test@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newUser)
                .contentType(ContentType.JSON).
        when()
            .post("/api/users").
        then()
            .assertThat()
                .statusCode(400)
                .body("statusCode", equalTo(400))
                .body("message", equalTo("name: must not be blank"))
                .body("uriRequested", equalTo("/api/users"));
    }

    @Test
    void whenAddDuplicatedUser_thenShouldGiveUnprocessableEntityError422() {
        User storedUser = addUser(new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );
        User newUser = new User(storedUser.getName(),
            storedUser.getEmail(),
            storedUser.getPassword(),
            storedUser.getRoles());

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newUser)
                .contentType(ContentType.JSON).
        when()
            .post("/api/users").
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", containsStringIgnoringCase("uc_user_email"))
                .body("uriRequested", equalTo("/api/users"));

        existUser(storedUser);
    }

    private void existUser(User user) {
        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/users/{id}", user.getId()).
        then()
            .assertThat()
                .statusCode(200)
                .body("id", equalTo((int)user.getId()))
                .body("name", equalTo(user.getName()))
                .body("email", equalTo(user.getEmail()))
                .body("password", equalTo(user.getPassword()))
                .body("roles", equalTo(user.getRoles()));
    }

    @Test
    void whenAddAdminUserWithUserRole_thenShouldGiveForbiddenError403() {
        User newUser = new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name())
        );

        given()
            .request()
                .cookie("AuthToken", authUserToken)
                .body(newUser)
                .contentType(ContentType.JSON).
        when()
            .post("/api/users").
        then()
            .assertThat()
                .statusCode(403);
    }

    @Test
    void whenAddAdminUserWithAdminRole_thenShouldAddAdminUser() {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        User newUser = new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name())
        );

        User addedUser =
            given()
                .request()
                    .cookie("AuthToken", authToken)
                    .body(newUser)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/users").
            then()
                .assertThat()
                    .statusCode(201)
                    .body("id", equalTo(3))
                    .body("name", equalTo(newUser.getName()))
                    .body("email", equalTo(newUser.getEmail()))
                    .body("password", equalTo("ZXhhbXBsZSBwYXNzd29yZA=="))
                    .body("roles", equalTo(newUser.getRoles()))
                    .extract().as(User.class);

        newUser.setId(addedUser.getId());
        newUser.setPassword("ZXhhbXBsZSBwYXNzd29yZA==");
        existUser(newUser);
    }

    @Test
    void testAddUser() {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        User newUser = new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.USER.name())
        );

        User addedUser =
            given()
                .request()
                    .cookie("AuthToken", authUserToken)
                    .body(newUser)
                    .contentType(ContentType.JSON).
            when()
                .post("/api/users").
            then()
                .assertThat()
                    .statusCode(201)
                    .body("id", equalTo(3))
                    .body("name", equalTo(newUser.getName()))
                    .body("email", equalTo(newUser.getEmail()))
                    .body("password", equalTo("ZXhhbXBsZSBwYXNzd29yZA=="))
                    .body("roles", equalTo(newUser.getRoles()))
                .extract().as(User.class);

        newUser.setId(addedUser.getId());
        newUser.setPassword("ZXhhbXBsZSBwYXNzd29yZA==");
        existUser(newUser);
    }

    @Test
    void whenUpdateNotExistUser_thenShouldGiveUserNotFoundError404() {
        User newUser = new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newUser)
                .contentType(ContentType.JSON).
        when()
            .put("/api/users/{id}", 3).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("User 3 not found"))
                .body("uriRequested", equalTo("/api/users/3"));
    }

    @Test
    void whenUpdateInvalidUser_thenShouldGiveBadRequestError400() {
        User storedUser = addUser(new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );
        User newUser = new User(" ",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newUser)
                .contentType(ContentType.JSON).
        when()
            .put("/api/users/{id}", storedUser.getId()).
        then()
            .assertThat()
                .statusCode(400)
                .body("statusCode", equalTo(400))
                .body("message", equalTo("name: must not be blank"))
                .body("uriRequested", equalTo("/api/users/" + storedUser.getId()));

        existUser(storedUser);
    }

    @Test
    void whenUpdateDuplicatedUser_thenShouldGiveUnprocessableEntityError422() {
        User storedUser1 = addUser(new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );
        User storedUser2 = addUser(new User("Distinct User",
            "other@gmail.com",
            "yYyYyYyYyYyYyYyYy==",
            List.of(UserRole.USER.name()))
        );
        User newUser = new User(storedUser2.getName(),
            storedUser2.getEmail(),
            storedUser2.getPassword(),
            storedUser2.getRoles()
        );

        given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newUser)
                .contentType(ContentType.JSON).
        when()
            .put("/api/users/{id}", storedUser1.getId()).
        then()
            .assertThat()
                .statusCode(422)
                .body("statusCode", equalTo(422))
                .body("message", containsStringIgnoringCase("uc_user_email"))
                .body("uriRequested", equalTo("/api/users/" + storedUser1.getId()));

        existUser(storedUser1);
        existUser(storedUser2);
    }

    @Test
    void whenUpdateAnotherUserWithUserRole_thenShouldGiveForbiddenError403() {
        User storedUser = addUser(new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );
        User newUser = new User("Distinct User",
            "other@gmail.com",
            "yYyYyYyYyYyYyYyYy==",
            List.of(UserRole.USER.name())
        );

        given()
            .request()
                .cookie("AuthToken", authUserToken)
                .body(newUser)
                .contentType(ContentType.JSON).
        when()
            .put("/api/users/{id}", storedUser.getId()).
        then()
            .assertThat()
                .statusCode(403);

        existUser(storedUser);
    }

    @Test
    void whenUpdateAnotherUserWithAdminRole_thenShouldUpdateAnotherUser() {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        User storedUser = addUser(new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );
        User newUser = new User("Distinct User",
            "other@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );

        User updatedUser =given()
            .request()
                .cookie("AuthToken", authToken)
                .body(newUser)
                .contentType(ContentType.JSON).
        when()
            .put("/api/users/{id}", storedUser.getId()).
        then()
            .assertThat()
                .statusCode(200)
                .body("id", equalTo((int)storedUser.getId()))
                .body("name", equalTo(newUser.getName()))
                .body("email", equalTo(newUser.getEmail()))
                .body("password", equalTo("ZXhhbXBsZSBwYXNzd29yZA=="))
                .body("roles", equalTo(newUser.getRoles()))
                .extract().as(User.class);

        newUser.setId(updatedUser.getId());
        newUser.setPassword("ZXhhbXBsZSBwYXNzd29yZA==");
        existUser(newUser);
    }
    @Test
    void testUpdateUser() {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        User newUser = new User("Distinct User",
            "other@gmail.com",
            "example password",
            List.of(UserRole.USER.name())
        );

        User updatedUser = given()
            .request()
                .cookie("AuthToken", authUserToken)
                .body(newUser)
                .contentType(ContentType.JSON).
        when()
            .put("/api/users/{id}", 2).
        then()
            .assertThat()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("name", equalTo(newUser.getName()))
                .body("email", equalTo(newUser.getEmail()))
                .body("password", equalTo("ZXhhbXBsZSBwYXNzd29yZA=="))
                .body("roles", equalTo(newUser.getRoles()))
            .extract().as(User.class);

        newUser.setId(updatedUser.getId());
        newUser.setPassword("ZXhhbXBsZSBwYXNzd29yZA==");
        existUser(newUser);
    }

    @Test
    void whenDeleteNotExistUser_thenShouldGiveUserNotFoundError404() {
        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .delete("/api/users/{id}", 3).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("User 3 not found"))
                .body("uriRequested", equalTo("/api/users/3"));
    }

    @Test
    void whenDeleteUserWithUserRole_thenShouldGiveForbiddenError403() {
        User storedUser = addUser(new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );

        given()
            .request()
                .cookie("AuthToken", authUserToken).
        when()
            .delete("/api/users/{id}", storedUser.getId()).
        then()
            .assertThat()
                .statusCode(403);

        existUser(storedUser);
    }

    @Test
    void testDeleteUser() {
        User storedUser = addUser(new User("Test User 3",
            "test3@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );

        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .delete("/api/users/{id}", storedUser.getId()).
        then()
            .assertThat()
                .statusCode(204);

        given()
            .request()
                .cookie("AuthToken", authToken).
        when()
            .get("/api/users/{id}", storedUser.getId()).
        then()
            .assertThat()
                .statusCode(404)
                .body("statusCode", equalTo(404))
                .body("message", equalTo("User " + storedUser.getId() + " not found"))
                .body("uriRequested", equalTo("/api/users/" + storedUser.getId()));
    }
}
