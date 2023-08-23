package com.example.demo.repository.criteria;

import com.example.demo.model.DayWeek;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Objects;

public class GeneratorCriteria {

    @Schema(description = "Name of the requested Generator", example = "Example name")
    private String name;
    @Schema(description = "CIF or DNI of the Generator's Society requested", example = "XXXXXXXXXX")
    private String societyCifDni;
    @Schema(description = "Name of the Generator's Society requested", example = "Example name")
    private String societyName;
    @Schema(description = "Name of the Generator's Generator Type requested", example = "Example name")
    private String generatorTypeName;
    @Schema(description = "Operational status of the requested Generator", example = "true")
    private Boolean active;
    @Schema(description = "Start of the range of Initialization date of operation of the requested Generator", example = "2023-01-01")
    private LocalDate startInitializationDate;
    @Schema(description = "Final of the range of Initialization date of operation of the requested Generator", example = "2023-01-15")
    private LocalDate finalInitializationDate;
    @Schema(description = "Start of the range of Termination date of operation of the requested Generator", example = "2023-12-25")
    private LocalDate startTerminationDate;
    @Schema(description = "Final of the range of Termination date of operation of the requested Generator", example = "2023-12-31")
    private LocalDate finalTerminationDate;
    @Schema(description = "Frequency of pick-up of the requested Generator", example = "10")
    private Integer periodicity;
    @Schema(description = "Day of the week of requested Generator pick-up", example = "LUNES", implementation = DayWeek.class)
    private String pickupDay;
    @Schema(description = "Day of the week off for the requested Generator", example = "DOMINGO", implementation = DayWeek.class)
    private String offDay;
    @Schema(description = "Email of the requested Generator", example = "example@gmail.com")
    private String email;
    @Schema(description = "Address of the requested Generator", example = "C. Tulipán")
    private String address;
    @Schema(description = "Phone number of the requested Generator", example = "123456789")
    private String phoneNumber;

    public GeneratorCriteria(String name, String societyCifDni, String societyName, String generatorTypeName, Boolean active, LocalDate startInitializationDate, LocalDate finalInitializationDate, LocalDate startTerminationDate, LocalDate finalTerminationDate, Integer periodicity, String pickupDay, String offDay, String email, String address, String phoneNumber) {
        this.name = name;
        this.societyCifDni = societyCifDni;
        this.societyName = societyName;
        this.generatorTypeName = generatorTypeName;
        this.active = active;
        this.startInitializationDate = startInitializationDate;
        this.finalInitializationDate = finalInitializationDate;
        this.startTerminationDate = startTerminationDate;
        this.finalTerminationDate = finalTerminationDate;
        this.periodicity = periodicity;
        this.pickupDay = pickupDay;
        this.offDay = offDay;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocietyCifDni() {
        return societyCifDni;
    }

    public void setSocietyCifDni(String societyCifDni) {
        this.societyCifDni = societyCifDni;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public String getGeneratorTypeName() {
        return generatorTypeName;
    }

    public void setGeneratorTypeName(String generatorTypeName) {
        this.generatorTypeName = generatorTypeName;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getStartInitializationDate() {
        return startInitializationDate;
    }

    public void setStartInitializationDate(LocalDate startInitializationDate) {
        this.startInitializationDate = startInitializationDate;
    }

    public LocalDate getFinalInitializationDate() {
        return finalInitializationDate;
    }

    public void setFinalInitializationDate(LocalDate finalInitializationDate) {
        this.finalInitializationDate = finalInitializationDate;
    }

    public LocalDate getStartTerminationDate() {
        return startTerminationDate;
    }

    public void setStartTerminationDate(LocalDate startTerminationDate) {
        this.startTerminationDate = startTerminationDate;
    }

    public LocalDate getFinalTerminationDate() {
        return finalTerminationDate;
    }

    public void setFinalTerminationDate(LocalDate finalTerminationDate) {
        this.finalTerminationDate = finalTerminationDate;
    }

    public Integer getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Integer periodicity) {
        this.periodicity = periodicity;
    }

    public String getPickupDay() {
        return pickupDay;
    }

    public void setPickupDay(String pickupDay) {
        this.pickupDay = pickupDay;
    }

    public String getOffDay() {
        return offDay;
    }

    public void setOffDay(String offDay) {
        this.offDay = offDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneratorCriteria that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(societyCifDni, that.societyCifDni) && Objects.equals(societyName, that.societyName) && Objects.equals(generatorTypeName, that.generatorTypeName) && Objects.equals(active, that.active) && Objects.equals(startInitializationDate, that.startInitializationDate) && Objects.equals(finalInitializationDate, that.finalInitializationDate) && Objects.equals(startTerminationDate, that.startTerminationDate) && Objects.equals(finalTerminationDate, that.finalTerminationDate) && Objects.equals(periodicity, that.periodicity) && Objects.equals(pickupDay, that.pickupDay) && Objects.equals(offDay, that.offDay) && Objects.equals(email, that.email) && Objects.equals(address, that.address) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, societyCifDni, societyName, generatorTypeName, active, startInitializationDate, finalInitializationDate, startTerminationDate, finalTerminationDate, periodicity, pickupDay, offDay, email, address, phoneNumber);
    }
}