package com.example.demo.unit.model;

import com.example.demo.model.GeneratorType;
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

@DisplayName("Generator Type model validation test")
class GeneratorTypeUnitTest {

    private Validator validator;
    private GeneratorType generatorType;

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        generatorType = new GeneratorType(1, "Test GeneratorType");
    }

    @Test
    void testGetId() {
        assertEquals(1, generatorType.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Test GeneratorType", generatorType.getName());
    }

    @Test
    void testSetId() {
        generatorType.setId(2);
        assertEquals(2, generatorType.getId());
    }

    @Test
    void testSetName() {
        generatorType.setName("Name changed");
        assertEquals("Name changed", generatorType.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        GeneratorType duplicatedGeneratorType = new GeneratorType(1, "Test GeneratorType");
        assertThat(generatorType.equals(duplicatedGeneratorType)).isTrue();
        assertEquals(generatorType.hashCode(), duplicatedGeneratorType.hashCode());

        GeneratorType differentGeneratorTypeName = new GeneratorType(1, "Distinct GeneratorType");
        assertThat(generatorType.equals(differentGeneratorTypeName)).isFalse();

        GeneratorType distinctGeneratorType = new GeneratorType(2, "Distinct GeneratorType");
        assertThat(generatorType.equals(distinctGeneratorType)).isFalse();
        assertNotEquals(generatorType.hashCode(), distinctGeneratorType.hashCode());

        assertThat(generatorType.equals(generatorType)).isTrue();
        assertThat(generatorType.equals(null)).isFalse();
        assertThat(generatorType.equals(new Object())).isFalse();
        assertThat(generatorType.equals(mock(GeneratorType.class))).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidNameScenarios")
    void whenInvalidName_thenShouldGiveConstraintViolations(String scenario, String name, String expectedMessage) {
        GeneratorType generatorType = new GeneratorType(name);
        Set<ConstraintViolation<GeneratorType>> violations = validator.validate(generatorType);
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
    void whenAllValid_thenShouldNotGiveConstraintViolations() {
        GeneratorType generatorType = new GeneratorType("Test GeneratorType");
        Set<ConstraintViolation<GeneratorType>> violations = validator.validate(generatorType);
        assertThat(violations).isEmpty();
    }
}