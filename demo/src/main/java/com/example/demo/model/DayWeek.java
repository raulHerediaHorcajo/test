package com.example.demo.model;

public enum DayWeek {
    MONDAY("LUNES"),
    TUESDAY("MARTES"),
    WEDNESDAY("MIÉRCOLES"),
    THURSDAY("JUEVES"),
    FRIDAY("VIERNES"),
    SATURDAY("SÁBADO"),
    SUNDAY("DOMINGO"),
    INDIFFERENT("INDIFERENTE");

    private final String day;

    DayWeek(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    @Override
    public String toString() {
        return day;
    }
}