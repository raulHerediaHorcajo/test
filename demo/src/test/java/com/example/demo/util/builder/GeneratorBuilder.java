package com.example.demo.util.builder;

import com.example.demo.model.DayWeek;
import com.example.demo.model.Generator;
import com.example.demo.model.GeneratorType;
import com.example.demo.model.Society;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class GeneratorBuilder {

    private long id = 0;
    private String name = "Test Generator";
    private Society society = new Society(1,"XXXXXXXXXX", "Test Society");
    private GeneratorType generatorType = new GeneratorType(1,"Test GeneratorType");
    private boolean active = true;
    private LocalDate initializationDate = LocalDate.of(2023, 1, 1);
    private LocalDate terminationDate = LocalDate.of(2023, 12, 31);
    private int periodicity = 10;
    private String pickupDay = DayWeek.MONDAY.getDay();
    private String offDay = DayWeek.SUNDAY.getDay();
    private LocalTime openingTime = LocalTime.of(9, 0);
    private LocalTime closingTime = LocalTime.of(20, 0);
    private String email = "test@gmail.com";
    private String address = "C. Tulipán";
    private List<String> phoneNumber = List.of("123456789", "987654321");
    private String observations = "Additional observations";

    public GeneratorBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public GeneratorBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public GeneratorBuilder withSociety(Society society) {
        this.society = society;
        return this;
    }

    public GeneratorBuilder withGeneratorType(GeneratorType generatorType) {
        this.generatorType = generatorType;
        return this;
    }

    public GeneratorBuilder withActive(boolean active) {
        this.active = active;
        return this;
    }

    public GeneratorBuilder withInitializationDate(LocalDate initializationDate) {
        this.initializationDate = initializationDate;
        return this;
    }

    public GeneratorBuilder withTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
        return this;
    }

    public GeneratorBuilder withPeriodicity(int periodicity) {
        this.periodicity = periodicity;
        return this;
    }

    public GeneratorBuilder withPickupDay(String pickupDay) {
        this.pickupDay = pickupDay;
        return this;
    }

    public GeneratorBuilder withOffDay(String offDay) {
        this.offDay = offDay;
        return this;
    }

    public GeneratorBuilder withOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
        return this;
    }

    public GeneratorBuilder withClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
        return this;
    }

    public GeneratorBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public GeneratorBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public GeneratorBuilder withPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public GeneratorBuilder withObservations(String observations) {
        this.observations = observations;
        return this;
    }

    public Generator build() {
        return new Generator(id, name, society, generatorType, active, initializationDate, terminationDate, periodicity, pickupDay, offDay, openingTime, closingTime, email, address, phoneNumber, observations);
    }
}