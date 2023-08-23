package com.example.demo.unit.model;

import com.example.demo.model.DayWeek;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DayWeekUnitTest {

    List<String> daysWeek = List.of("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO", "INDIFERENTE");

    @ParameterizedTest
    @EnumSource(DayWeek.class)
    void testGetDay(DayWeek day) {
        assertEquals(day.getDay(), daysWeek.get(day.ordinal()));
    }

    @ParameterizedTest
    @EnumSource(DayWeek.class)
    void testToString(DayWeek day) {
        assertEquals(day.toString(), daysWeek.get(day.ordinal()));
    }
}