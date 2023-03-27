package com.example.demo.unit.model;

import com.example.demo.model.Society;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

@DisplayName("Society model validation test")
class SocietyUnitTest {

    private Validator validator;
    private Society society;

    /*@BeforeAll
    public static void setUpClass() {
        System.out.println("Before all tests");
    }*/

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        society = new Society(1, "XXXXXXXXXX", "Test Society");
    }

    @Test
    void testGetId() {
        assertEquals(1, society.getId());
    }

    @Test
    void testGetCifDni() {
        assertEquals("XXXXXXXXXX", society.getCifDni());
    }

    @Test
    void testGetName() {
        assertEquals("Test Society", society.getName());
    }

    @Test
    void testSetId() {
        society.setId(2);
        assertEquals(2, society.getId());
    }

    @Test
    void testSetCifDni() {
        society.setCifDni("YYYYYYYYYY");
        assertEquals("YYYYYYYYYY", society.getCifDni());
    }

    @Test
    void testSetName() {
        society.setName("Name changed");
        assertEquals("Name changed", society.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        Society duplicatedSociety = new Society(1, "XXXXXXXXXX", "Test Society");
        assertThat(society.equals(duplicatedSociety)).isTrue();
        assertEquals(society.hashCode(), duplicatedSociety.hashCode());

        Society differentSocietyName = new Society(1, "XXXXXXXXXX", "Distinct Society");
        assertThat(society.equals(differentSocietyName)).isFalse();

        Society distinctSociety = new Society(2, "YYYYYYYYYY", "Distinct Society");
        assertThat(society.equals(distinctSociety)).isFalse();
        assertNotEquals(society.hashCode(), distinctSociety.hashCode());

        assertThat(society.equals(society)).isTrue();
        assertThat(society.equals(null)).isFalse();
        assertThat(society.equals(new Object())).isFalse();
        assertThat(society.equals(mock(Society.class))).isFalse();
    }

    //@Test
    //@DisplayName("Test 1")
    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCifDniScenarios")
    void whenInvalidCifDni_thenShouldGiveConstraintViolations(String scenario, String cifDni, String expectedMessage) {
        Society society = new Society(cifDni, "name");
        Set<ConstraintViolation<Society>> violations = validator.validate(society);
        assertThat(violations)
        .anyMatch( l -> ("cifDni".equals(l.getPropertyPath().toString())) && (expectedMessage.equals(l.getMessage())));

    }
    private static Stream<Arguments> invalidCifDniScenarios() {
        return Stream.of(
                arguments("cifDni is null", null, "must not be blank"),
                arguments("cifDni is empty", "", "must not be blank"),
                arguments("cifDni is blank", " ", "must not be blank")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidNameScenarios")
    void whenInvalidName_thenShouldGiveConstraintViolations(String scenario, String name, String expectedMessage) {
        Society society = new Society("cifDni", name);
        Set<ConstraintViolation<Society>> violations = validator.validate(society);
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

    @Test
    //@DisplayName("Test 1")
    void whenAllValid_thenShouldNotGiveConstraintViolations() {
        Society society = new Society("cifDni", "name");
        Set<ConstraintViolation<Society>> violations = validator.validate(society);
        assertThat(violations).isEmpty();
    }
}