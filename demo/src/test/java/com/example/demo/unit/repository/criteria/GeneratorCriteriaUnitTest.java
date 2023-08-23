package com.example.demo.unit.repository.criteria;

import com.example.demo.model.DayWeek;
import com.example.demo.repository.criteria.GeneratorCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

class GeneratorCriteriaUnitTest {

    private GeneratorCriteria generatorCriteria;

    @BeforeEach
    public void setUp() {
        generatorCriteria = new GeneratorCriteria(
            "Test Generator",
            "XXXXXXXXXX",
            "Test Society",
            "Test GeneratorType",
            true,
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 15),
            LocalDate.of(2023, 12, 25),
            LocalDate.of(2023, 12, 31),
            10,
            DayWeek.MONDAY.getDay(),
            DayWeek.SUNDAY.getDay(),
            "test@gmail.com",
            "C. Tulipán",
            "123456789"
        );
    }

    @Test
    void testGetName() {
        assertEquals("Test Generator", generatorCriteria.getName());
    }

    @Test
    void testSetName() {
        generatorCriteria.setName("Name changed");
        assertEquals("Name changed", generatorCriteria.getName());
    }

    @Test
    void testGetSocietyCifDni() {
        assertEquals("XXXXXXXXXX", generatorCriteria.getSocietyCifDni());
    }

    @Test
    void testSetSocietyCifDni() {
        generatorCriteria.setSocietyCifDni("YYYYYYYYYY");
        assertEquals("YYYYYYYYYY", generatorCriteria.getSocietyCifDni());
    }

    @Test
    void testGetSocietyName() {
        assertEquals("Test Society", generatorCriteria.getSocietyName());
    }

    @Test
    void testSetSocietyName() {
        generatorCriteria.setSocietyName("Society name changed");
        assertEquals("Society name changed", generatorCriteria.getSocietyName());
    }

    @Test
    void testGetGeneratorTypeName() {
        assertEquals("Test GeneratorType", generatorCriteria.getGeneratorTypeName());
    }

    @Test
    void testSetGeneratorTypeName() {
        generatorCriteria.setGeneratorTypeName("GeneratorType name changed");
        assertEquals("GeneratorType name changed", generatorCriteria.getGeneratorTypeName());
    }

    @Test
    void testIsActive() {
        assertEquals(true, generatorCriteria.isActive());
    }

    @Test
    void testSetActive() {
        generatorCriteria.setActive(false);
        assertEquals(false, generatorCriteria.isActive());
    }

    @Test
    void testGetStartInitializationDate() {
        assertEquals(LocalDate.of(2023, 1, 1), generatorCriteria.getStartInitializationDate());
    }

    @Test
    void testSetStartInitializationDate() {
        generatorCriteria.setStartInitializationDate(LocalDate.of(2024, 1, 1));
        assertEquals(LocalDate.of(2024, 1, 1), generatorCriteria.getStartInitializationDate());
    }

    @Test
    void testGetFinalInitializationDate() {
        assertEquals(LocalDate.of(2023, 1, 15), generatorCriteria.getFinalInitializationDate());
    }

    @Test
    void testSetFinalInitializationDate() {
        generatorCriteria.setFinalInitializationDate(LocalDate.of(2024, 1, 15));
        assertEquals(LocalDate.of(2024, 1, 15), generatorCriteria.getFinalInitializationDate());
    }

    @Test
    void testGetStartTerminationDate() {
        assertEquals(LocalDate.of(2023, 12, 25), generatorCriteria.getStartTerminationDate());
    }

    @Test
    void testSetStartTerminationDate() {
        generatorCriteria.setStartTerminationDate(LocalDate.of(2024, 12, 25));
        assertEquals(LocalDate.of(2024, 12, 25), generatorCriteria.getStartTerminationDate());
    }

    @Test
    void testGetFinalTerminationDate() {
        assertEquals(LocalDate.of(2023, 12, 31), generatorCriteria.getFinalTerminationDate());
    }

    @Test
    void testSetFinalTerminationDate() {
        generatorCriteria.setFinalTerminationDate(LocalDate.of(2024, 12, 31));
        assertEquals(LocalDate.of(2024, 12, 31), generatorCriteria.getFinalTerminationDate());
    }

    @Test
    void testGetPeriodicity() {
        assertEquals(10, generatorCriteria.getPeriodicity());
    }

    @Test
    void testSetPeriodicity() {
        generatorCriteria.setPeriodicity(20);
        assertEquals(20, generatorCriteria.getPeriodicity());
    }

    @Test
    void testGetPickupDay() {
        assertEquals(DayWeek.MONDAY.getDay(), generatorCriteria.getPickupDay());
    }

    @Test
    void testSetPickupDay() {
        generatorCriteria.setPickupDay(DayWeek.WEDNESDAY.getDay());
        assertEquals(DayWeek.WEDNESDAY.getDay(), generatorCriteria.getPickupDay());
    }

    @Test
    void testGetOffDay() {
        assertEquals(DayWeek.SUNDAY.getDay(), generatorCriteria.getOffDay());
    }

    @Test
    void testSetOffDay() {
        generatorCriteria.setOffDay(DayWeek.SATURDAY.getDay());
        assertEquals(DayWeek.SATURDAY.getDay(), generatorCriteria.getOffDay());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@gmail.com", generatorCriteria.getEmail());
    }

    @Test
    void testSetEmail() {
        generatorCriteria.setEmail("other@gmail.com");
        assertEquals("other@gmail.com", generatorCriteria.getEmail());
    }

    @Test
    void testGetAddress() {
        assertEquals("C. Tulipán", generatorCriteria.getAddress());
    }

    @Test
    void testSetAddress() {
        generatorCriteria.setAddress("C. Other");
        assertEquals("C. Other", generatorCriteria.getAddress());
    }

    @Test
    void testGetPhoneNumber() {
        assertEquals("123456789", generatorCriteria.getPhoneNumber());
    }

    @Test
    void testSetPhoneNumber() {
        generatorCriteria.setPhoneNumber("111111111");
        assertEquals("111111111", generatorCriteria.getPhoneNumber());
    }

    @Test
    void testEqualsAndHashCode() {
        GeneratorCriteria newGeneratorCriteria = new GeneratorCriteria(
            "Test Generator",
            "XXXXXXXXXX",
            "Test Society",
            "Test GeneratorType",
            true,
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 15),
            LocalDate.of(2023, 12, 25),
            LocalDate.of(2023, 12, 31),
            10,
            DayWeek.MONDAY.getDay(),
            DayWeek.SUNDAY.getDay(),
            "test@gmail.com",
            "C. Tulipán",
            "123456789"
        );
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isTrue();
        assertEquals(generatorCriteria.hashCode(), newGeneratorCriteria.hashCode());

        newGeneratorCriteria.setName("Distinct Generator");
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setName("Test Generator");
        newGeneratorCriteria.setSocietyCifDni("YYYYYYYYYY");
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setSocietyCifDni("XXXXXXXXXX");
        newGeneratorCriteria.setSocietyName("Society name changed");
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setSocietyName("Test Society");
        newGeneratorCriteria.setGeneratorTypeName("GeneratorType name changed");
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setGeneratorTypeName("Test GeneratorType");
        newGeneratorCriteria.setActive(false);
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setActive(true);
        newGeneratorCriteria.setStartInitializationDate(LocalDate.of(2024, 1, 1));
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setStartInitializationDate(LocalDate.of(2023, 1, 1));
        newGeneratorCriteria.setFinalInitializationDate(LocalDate.of(2024, 1, 15));
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setFinalInitializationDate(LocalDate.of(2023, 1, 15));
        newGeneratorCriteria.setStartTerminationDate(LocalDate.of(2024, 12, 25));
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setStartTerminationDate(LocalDate.of(2023, 12, 25));
        newGeneratorCriteria.setFinalTerminationDate(LocalDate.of(2024, 12, 31));
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setFinalTerminationDate(LocalDate.of(2023, 12, 31));
        newGeneratorCriteria.setPeriodicity(20);
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setPeriodicity(10);
        newGeneratorCriteria.setPickupDay(DayWeek.WEDNESDAY.getDay());
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setPickupDay(DayWeek.MONDAY.getDay());
        newGeneratorCriteria.setOffDay(DayWeek.SATURDAY.getDay());
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setOffDay(DayWeek.SUNDAY.getDay());
        newGeneratorCriteria.setEmail("other@gmail.com");
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setEmail("test@gmail.com");
        newGeneratorCriteria.setAddress("C. Other");
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();

        newGeneratorCriteria.setAddress("C. Tulipán");
        newGeneratorCriteria.setPhoneNumber("111111111");
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();


        newGeneratorCriteria.setName("Distinct Generator");
        newGeneratorCriteria.setSocietyCifDni("YYYYYYYYYY");
        newGeneratorCriteria.setSocietyName("Society name changed");
        newGeneratorCriteria.setGeneratorTypeName("GeneratorType name changed");
        newGeneratorCriteria.setActive(false);
        newGeneratorCriteria.setStartInitializationDate(LocalDate.of(2024, 1, 1));
        newGeneratorCriteria.setFinalInitializationDate(LocalDate.of(2024, 1, 15));
        newGeneratorCriteria.setStartTerminationDate(LocalDate.of(2024, 12, 25));
        newGeneratorCriteria.setFinalTerminationDate(LocalDate.of(2024, 12, 31));
        newGeneratorCriteria.setPeriodicity(20);
        newGeneratorCriteria.setPickupDay(DayWeek.WEDNESDAY.getDay());
        newGeneratorCriteria.setOffDay(DayWeek.SATURDAY.getDay());
        newGeneratorCriteria.setEmail("other@gmail.com");
        newGeneratorCriteria.setAddress("C. Other");
        assertThat(generatorCriteria.equals(newGeneratorCriteria)).isFalse();
        assertNotEquals(generatorCriteria.hashCode(), newGeneratorCriteria.hashCode());

        assertThat(generatorCriteria.equals(generatorCriteria)).isTrue();
        assertThat(generatorCriteria.equals(null)).isFalse();
        assertThat(generatorCriteria.equals(new Object())).isFalse();
        assertThat(generatorCriteria.equals(mock(GeneratorCriteria.class))).isFalse();
    }
}