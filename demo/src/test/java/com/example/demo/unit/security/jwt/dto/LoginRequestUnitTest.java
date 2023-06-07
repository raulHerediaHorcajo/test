package com.example.demo.unit.security.jwt.dto;

import com.example.demo.security.jwt.dto.LoginRequest;
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

import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("LoginRequest model tests")
class LoginRequestUnitTest {

    private Validator validator;
    private LoginRequest loginRequest;

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        loginRequest = new LoginRequest(
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA=="
        );
    }

    @Test
    void testGetUsername() {
        assertEquals("test@gmail.com", loginRequest.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("ZXhhbXBsZSBwYXNzd29yZA==", loginRequest.getPassword());
    }

    @Test
    void testSetUsername() {
        loginRequest.setUsername("other@gmail.com");
        assertEquals("other@gmail.com", loginRequest.getUsername());
    }

    @Test
    void testSetPassword() {
        loginRequest.setPassword("yYyYyYyYyYyYyYyYy==");
        assertEquals("yYyYyYyYyYyYyYyYy==", loginRequest.getPassword());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUsernameScenarios")
    void whenInvalidUsername_thenShouldGiveConstraintViolations(String scenario, String username, String expectedMessage) {
        LoginRequest loginRequest = new LoginRequest(username, "ZXhhbXBsZSBwYXNzd29yZA==");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertThat(violations)
            .anyMatch( l -> ("username".equals(l.getPropertyPath().toString())) && (expectedMessage.equals(l.getMessage())));

    }
    private static Stream<Arguments> invalidUsernameScenarios() {
        return Stream.of(
            arguments("username is null", null, "must not be blank"),
            arguments("username is empty", "", "must not be blank"),
            arguments("username is blank", " ", "must not be blank"),
            arguments("username is invalid", "Invalid email", "must be a well-formed email address")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidPasswordScenarios")
    void whenInvalidPassword_thenShouldGiveConstraintViolations(String scenario, String password, String expectedMessage) {
        LoginRequest loginRequest = new LoginRequest("test@gmail.com", password);
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
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
}