package com.example.demo.unit.model;

import com.example.demo.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

@DisplayName("User model validation test")
class UserUnitTest {

    private Validator validator;
    private User user;

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of("ADMIN", "USER")
        );
    }

    @Test
    void testGetId() {
        assertEquals(1, user.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Test User", user.getName());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@gmail.com", user.getEmail());
    }

    @Test
    void testGetPassword() {
        assertEquals("ZXhhbXBsZSBwYXNzd29yZA==", user.getPassword());
    }

    @Test
    void testGetRoles() {
        assertEquals(List.of("ADMIN", "USER"), user.getRoles());
    }

    @Test
    void testSetId() {
        user.setId(2);
        assertEquals(2, user.getId());
    }

    @Test
    void testSetName() {
        user.setName("Name changed");
        assertEquals("Name changed", user.getName());
    }

    @Test
    void testSetEmail() {
        user.setEmail("other@gmail.com");
        assertEquals("other@gmail.com", user.getEmail());
    }

    @Test
    void testSetPassword() {
        user.setPassword("yYyYyYyYyYyYyYyYy==");
        assertEquals("yYyYyYyYyYyYyYyYy==", user.getPassword());
    }

    @Test
    void testSetRoles() {
        user.setRoles(List.of("USER"));
        assertEquals(List.of("USER"), user.getRoles());
    }

    @Test
    void testEqualsAndHashCode() {
        User duplicatedUser = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of("ADMIN", "USER")
        );
        assertThat(user.equals(duplicatedUser)).isTrue();
        assertEquals(user.hashCode(), duplicatedUser.hashCode());

        User differentUserName = new User(1,
            "Distinct User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of("ADMIN", "USER"));
        assertThat(user.equals(differentUserName)).isFalse();

        /*User differentUserEmail = new User(1,
            "Test User",
            "other@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of("ADMIN", "USER"));
        assertThat(user.equals(differentUserEmail)).isFalse();*/

        User differentUserPassword = new User(1,
            "Test User",
            "test@gmail.com",
            "yYyYyYyYyYyYyYyYy==",
            List.of("ADMIN", "USER"));
        assertThat(user.equals(differentUserPassword)).isFalse();

        User differentUserRoles = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of("USER"));
        assertThat(user.equals(differentUserRoles)).isFalse();

        User distinctUser = new User(2,
            "Distinct User",
            "other@gmail.com",
            "yYyYyYyYyYyYyYyYy==",
            List.of("USER"));
        assertThat(user.equals(distinctUser)).isFalse();
        assertNotEquals(user.hashCode(), distinctUser.hashCode());

        assertThat(user.equals(user)).isTrue();
        assertThat(user.equals(null)).isFalse();
        assertThat(user.equals(new Object())).isFalse();
        assertThat(user.equals(mock(User.class))).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidNameScenarios")
    void whenInvalidName_thenShouldGiveConstraintViolations(String scenario, String name, String expectedMessage) {
        User user = new User(name,
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of("ADMIN", "USER"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations)
            .anyMatch( l -> ("name".equals(l.getPropertyPath().toString())) && (expectedMessage.equals(l.getMessage())));

    }
    private static Stream<Arguments> invalidNameScenarios() {
        return Stream.of(
            arguments("name is null", null, "must not be blank"),
            arguments("name is empty", "", "must not be blank"),
            arguments("name is blank", " ", "must not be blank")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidEmailScenarios")
    void whenInvalidEmail_thenShouldGiveConstraintViolations(String scenario, String email, String expectedMessage) {
        User user = new User("Test User",
            email,
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of("ADMIN", "USER"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations)
            .anyMatch( l -> ("email".equals(l.getPropertyPath().toString())) && (expectedMessage.equals(l.getMessage())));

    }
    private static Stream<Arguments> invalidEmailScenarios() {
        return Stream.of(
            arguments("email is null", null, "must not be blank"),
            arguments("email is empty", "", "must not be blank"),
            arguments("email is blank", " ", "must not be blank"),
            arguments("email is invalid", "Invalid email", "must be a well-formed email address")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidPasswordScenarios")
    void whenInvalidPassword_thenShouldGiveConstraintViolations(String scenario, String password, String expectedMessage) {
        User user = new User("Test User",
            "test@gmail.com",
            password,
            List.of("ADMIN", "USER"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations)
            .anyMatch( l -> ("password".equals(l.getPropertyPath().toString())) && (expectedMessage.equals(l.getMessage())));

    }
    private static Stream<Arguments> invalidPasswordScenarios() {
        return Stream.of(
            arguments("password is null", null, "must not be blank"),
            arguments("password is empty", "", "must not be blank"),
            arguments("password is blank", " ", "must not be blank")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRolesScenarios")
    void whenInvalidRoles_thenShouldGiveConstraintViolations(String scenario, List<String> roles, String expectedMessage) {
        User user = new User("Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations)
            .anyMatch( l -> (l.getPropertyPath().toString().contains("roles")) && (expectedMessage.equals(l.getMessage())));

    }
    private static Stream<Arguments> invalidRolesScenarios() {
        return Stream.of(
            arguments("roles is null", null, "must not be null"),
            arguments("roles is empty", List.of(), "size must be between 1 and 2"),
            arguments("roles has too many roles", List.of("ADMIN", "ADMIN", "ADMIN"), "size must be between 1 and 2"),
            arguments("roles has an invalid role", List.of("INVALID"), "must match \"^(ADMIN|USER)$\"")
        );
    }

    @Test
    void whenAllValid_thenShouldNotGiveConstraintViolations() {
        User user = new User("Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of("ADMIN", "USER"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }
}