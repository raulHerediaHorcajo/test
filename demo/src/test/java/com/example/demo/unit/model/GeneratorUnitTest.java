package com.example.demo.unit.model;

import com.example.demo.model.DayWeek;
import com.example.demo.model.Generator;
import com.example.demo.model.GeneratorType;
import com.example.demo.model.Society;
import com.example.demo.util.builder.GeneratorBuilder;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

@DisplayName("Generator model validation test")
class GeneratorUnitTest {

    private Validator validator;
    private Generator  generator;

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        generator = new GeneratorBuilder().build();
    }

    @Test
    void testGetId() {
        assertEquals(0, generator.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Test Generator", generator.getName());
    }

    @Test
    void testGetSociety() {
        assertEquals(new Society(1, "XXXXXXXXXX", "Test Society"), generator.getSociety());
    }

    @Test
    void testGetGeneratorType() {
        assertEquals(new GeneratorType(1, "Test GeneratorType"), generator.getGeneratorType());
    }

    @Test
    void testIsActive() {
        assertTrue(generator.isActive());
    }

    @Test
    void testGetInitializationDate() {
        assertEquals(LocalDate.of(2023, 1, 1), generator.getInitializationDate());
    }

    @Test
    void testGetTerminationDate() {
        assertEquals(LocalDate.of(2023, 12, 31), generator.getTerminationDate());
    }

    @Test
    void testGetPeriodicity() {
        assertEquals(10, generator.getPeriodicity());
    }

    @Test
    void testGetPickupDay() {
        assertEquals(DayWeek.MONDAY.getDay(), generator.getPickupDay());
    }

    @Test
    void testGetOffDay() {
        assertEquals(DayWeek.SUNDAY.getDay(), generator.getOffDay());
    }

    @Test
    void testGetOpeningTime() {
        assertEquals(LocalTime.of(9, 0), generator.getOpeningTime());
    }

    @Test
    void testGetClosingTime() {
        assertEquals(LocalTime.of(20, 0), generator.getClosingTime());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@gmail.com", generator.getEmail());
    }

    @Test
    void testGetAddress() {
        assertEquals("C. Tulipán", generator.getAddress());
    }

    @Test
    void testGetPhoneNumber() {
        assertEquals(List.of("123456789", "987654321"), generator.getPhoneNumber());
    }

    @Test
    void testGetObservations() {
        assertEquals("Additional observations", generator.getObservations());
    }

    @Test
    void testSetId() {
        generator.setId(1);
        assertEquals(1, generator.getId());
    }

    @Test
    void testSetName() {
        generator.setName("Name changed");
        assertEquals("Name changed", generator.getName());
    }

    @Test
    void testSetSociety() {
        Society changedSociety = mock(Society.class);
        generator.setSociety(changedSociety);
        assertEquals(changedSociety, generator.getSociety());
    }

    @Test
    void testSetGeneratorType() {
        GeneratorType changedGeneratorType = mock(GeneratorType.class);
        generator.setGeneratorType(changedGeneratorType);
        assertEquals(changedGeneratorType, generator.getGeneratorType());
    }

    @Test
    void testSetActive() {
        generator.setActive(false);
        assertFalse(generator.isActive());
    }

    @Test
    void testSetInitializationDate() {
        generator.setInitializationDate(LocalDate.of(2024, 1, 1));
        assertEquals(LocalDate.of(2024, 1, 1), generator.getInitializationDate());
    }

    @Test
    void testSetTerminationDate() {
        generator.setTerminationDate(LocalDate.of(2024, 12, 31));
        assertEquals(LocalDate.of(2024, 12, 31), generator.getTerminationDate());
    }

    @Test
    void testSetPeriodicity() {
        generator.setPeriodicity(20);
        assertEquals(20, generator.getPeriodicity());
    }

    @Test
    void testSetPickupDay() {
        generator.setPickupDay(DayWeek.WEDNESDAY.getDay());
        assertEquals(DayWeek.WEDNESDAY.getDay(), generator.getPickupDay());
    }

    @Test
    void testSetOffDay() {
        generator.setOffDay(DayWeek.SATURDAY.getDay());
        assertEquals(DayWeek.SATURDAY.getDay(), generator.getOffDay());
    }

    @Test
    void testSetOpeningTime() {
        generator.setOpeningTime(LocalTime.of(7, 0));
        assertEquals(LocalTime.of(7, 0), generator.getOpeningTime());
    }

    @Test
    void testSetClosingTime() {
        generator.setClosingTime(LocalTime.of(22, 0));
        assertEquals(LocalTime.of(22, 0), generator.getClosingTime());
    }

    @Test
    void testSetEmail() {
        generator.setEmail("other@gmail.com");
        assertEquals("other@gmail.com", generator.getEmail());
    }

    @Test
    void testSetAddress() {
        generator.setAddress("C. Other");
        assertEquals("C. Other", generator.getAddress());
    }

    @Test
    void testSetPhoneNumber() {
        generator.setPhoneNumber(List.of("111111111", "222222222"));
        assertEquals(List.of("111111111", "222222222"), generator.getPhoneNumber());
    }

    @Test
    void testSetObservations() {
        generator.setObservations("Other observations");
        assertEquals("Other observations", generator.getObservations());
    }

    @Test
    void testEqualsAndHashCode() {
        Generator duplicatedGenerator = new GeneratorBuilder().build();
        assertThat(generator.equals(duplicatedGenerator)).isTrue();
        assertEquals(generator.hashCode(), duplicatedGenerator.hashCode());

        Generator differentGeneratorName = new GeneratorBuilder().withName("Distinct Generator").build();
        assertThat(generator.equals(differentGeneratorName)).isFalse();

        Generator differentGeneratorSociety = new GeneratorBuilder().withSociety(mock(Society.class)).build();
        assertThat(generator.equals(differentGeneratorSociety)).isFalse();

        Generator differentGeneratorGeneratorType = new GeneratorBuilder().withGeneratorType(mock(GeneratorType.class)).build();
        assertThat(generator.equals(differentGeneratorGeneratorType)).isFalse();

        Generator differentGeneratorActive = new GeneratorBuilder().withActive(false).build();
        assertThat(generator.equals(differentGeneratorActive)).isFalse();

        Generator differentGeneratorInitializationDate = new GeneratorBuilder().withInitializationDate(LocalDate.of(2024, 1, 1)).build();
        assertThat(generator.equals(differentGeneratorInitializationDate)).isFalse();

        Generator differentGeneratorTerminationDate = new GeneratorBuilder().withTerminationDate(LocalDate.of(2024, 12, 31)).build();
        assertThat(generator.equals(differentGeneratorTerminationDate)).isFalse();

        Generator differentGeneratorPeriodicity = new GeneratorBuilder().withPeriodicity(20).build();
        assertThat(generator.equals(differentGeneratorPeriodicity)).isFalse();

        Generator differentGeneratorPickupDay = new GeneratorBuilder().withPickupDay(DayWeek.WEDNESDAY.getDay()).build();
        assertThat(generator.equals(differentGeneratorPickupDay)).isFalse();

        Generator differentGeneratorOffDay = new GeneratorBuilder().withOffDay(DayWeek.SATURDAY.getDay()).build();
        assertThat(generator.equals(differentGeneratorOffDay)).isFalse();

        Generator differentGeneratorOpeningTime = new GeneratorBuilder().withOpeningTime(LocalTime.of(7, 0)).build();
        assertThat(generator.equals(differentGeneratorOpeningTime)).isFalse();

        Generator differentGeneratorClosingTime = new GeneratorBuilder().withClosingTime(LocalTime.of(22, 0)).build();
        assertThat(generator.equals(differentGeneratorClosingTime)).isFalse();

        Generator differentGeneratorEmail = new GeneratorBuilder().withEmail("other@gmail.com").build();
        assertThat(generator.equals(differentGeneratorEmail)).isFalse();

        Generator differentGeneratorAddress = new GeneratorBuilder().withAddress("C. Other").build();
        assertThat(generator.equals(differentGeneratorAddress)).isFalse();

        Generator differentGeneratorPhoneNumber = new GeneratorBuilder().withPhoneNumber(List.of("111111111", "222222222")).build();
        assertThat(generator.equals(differentGeneratorPhoneNumber)).isFalse();

        Generator differentGeneratorObservations = new GeneratorBuilder().withObservations("Other observations").build();
        assertThat(generator.equals(differentGeneratorObservations)).isFalse();

        Generator distinctGenerator = new GeneratorBuilder()
            .withId(2)
            .withName("Distinct Generator")
            .withSociety(mock(Society.class))
            .withGeneratorType(mock(GeneratorType.class))
            .withActive(false)
            .withInitializationDate(LocalDate.of(2024, 1, 1))
            .withTerminationDate(LocalDate.of(2024, 12, 31))
            .withPeriodicity(20)
            .withPickupDay(DayWeek.WEDNESDAY.getDay())
            .withOffDay(DayWeek.SATURDAY.getDay())
            .withOpeningTime(LocalTime.of(7, 0))
            .withClosingTime(LocalTime.of(22, 0))
            .withEmail("other@gmail.com")
            .withAddress("C. Other")
            .withPhoneNumber(List.of("111111111", "222222222"))
            .withObservations("Other observations")
            .build();
        assertThat(generator.equals(distinctGenerator)).isFalse();
        assertNotEquals(generator.hashCode(), distinctGenerator.hashCode());

        assertThat(generator.equals(generator)).isTrue();
        assertThat(generator.equals(null)).isFalse();
        assertThat(generator.equals(new Object())).isFalse();
        assertThat(generator.equals(mock(Generator.class))).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidNameScenarios")
    void whenInvalidName_thenShouldGiveConstraintViolations(String scenario, String name, String expectedMessage) {
        generator.setName(name);
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
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
    void whenSocietyIsNull_thenShouldGiveConstraintViolations() {
        generator.setSociety(null);
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
        assertThat(violations)
            .anyMatch( l -> ("society".equals(l.getPropertyPath().toString())) && ("must not be null".equals(l.getMessage())));
    }

    @Test
    void whenGeneratorTypeIsNull_thenShouldGiveConstraintViolations() {
        generator.setGeneratorType(null);
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
        assertThat(violations)
            .anyMatch( l -> ("generatorType".equals(l.getPropertyPath().toString())) && ("must not be null".equals(l.getMessage())));
    }

    @Test
    void whenInitializationDateIsNull_thenShouldGiveConstraintViolations() {
        generator.setInitializationDate(null);
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
        assertThat(violations)
            .anyMatch( l -> ("initializationDate".equals(l.getPropertyPath().toString())) && ("must not be null".equals(l.getMessage())));
    }

    @Test
    void whenPickupDayIsNull_thenShouldGiveConstraintViolations() {
        generator.setPickupDay("INVALID");
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
        assertThat(violations)
            .anyMatch( l -> ("pickupDay".equals(l.getPropertyPath().toString())) && ("must be any of enum class com.example.demo.model.DayWeek".equals(l.getMessage())));
    }

    @Test
    void whenOffDayIsNull_thenShouldGiveConstraintViolations() {
        generator.setOffDay("INVALID");
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
        assertThat(violations)
            .anyMatch( l -> ("offDay".equals(l.getPropertyPath().toString())) && ("must be any of enum class com.example.demo.model.DayWeek".equals(l.getMessage())));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidEmailScenarios")
    void whenInvalidEmail_thenShouldGiveConstraintViolations(String scenario, String email, String expectedMessage) {
        generator.setEmail(email);
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
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
    @MethodSource("invalidAddressScenarios")
    void whenInvalidAddress_thenShouldGiveConstraintViolations(String scenario, String address, String expectedMessage) {
        generator.setAddress(address);
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
        assertThat(violations)
            .anyMatch( l -> ("address".equals(l.getPropertyPath().toString())) && (expectedMessage.equals(l.getMessage())));
    }
    private static Stream<Arguments> invalidAddressScenarios() {
        return Stream.of(
            arguments("address is null", null, "must not be blank"),
            arguments("address is empty", "", "must not be blank"),
            arguments("address is blank", " ", "must not be blank")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidPhoneNumberScenarios")
    void whenInvalidPhoneNumber_thenShouldGiveConstraintViolations(String scenario, List<String> phoneNumber, String expectedMessage) {
        generator.setPhoneNumber(phoneNumber);
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
        assertThat(violations)
            .anyMatch( l -> (l.getPropertyPath().toString().contains("phoneNumber")) && (expectedMessage.equals(l.getMessage())));

    }
    private static Stream<Arguments> invalidPhoneNumberScenarios() {
        return Stream.of(
            arguments("Phone number is null", null, "must not be empty"),
            arguments("Phone number is empty", List.of(), "must not be empty"),
            arguments("Phone number has equal roles", List.of("123456789", "123456789"), "must only contain unique elements"),
            arguments("Phone number has an invalid Phone number", List.of("INVALID"), "must match \"\\d{9}\"")
        );
    }

    @Test
    void whenAllValid_thenShouldNotGiveConstraintViolations() {
        Set<ConstraintViolation<Generator>> violations = validator.validate(generator);
        assertThat(violations).isEmpty();
    }
}