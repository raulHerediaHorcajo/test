package com.example.demo.model;

import com.example.demo.model.validation.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Schema(description = "Represents the information of a Generator")
@Entity
public class Generator {

    @Schema(description = "Generator Identifier", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Schema(description = "Generator name", example = "Example name")
    @Column(unique = true, nullable = false)
    @NotBlank
    private String name;
    @ManyToOne(optional = false)
    @NotNull
    @Valid
    private Society society;
    @ManyToOne(optional = false)
    @NotNull
    @Valid
    private GeneratorType generatorType;

    @Schema(description = "Generator operational status", example = "true")
    private boolean active;
    @Schema(description = "Date of initialization of generator operation", example = "2023-01-01")
    @NotNull
    private LocalDate initializationDate;
    @Schema(description = "Date of termination of generator operation", example = "2023-12-31")
    private LocalDate terminationDate;

    @Schema(description = "Frequency of generator pick-up", example = "10")
    private int periodicity;
    @Schema(description = "Day of the week of generator pick-up", example = "LUNES", implementation = DayWeek.class)
    @ValueOfEnum(enumClass = DayWeek.class)
    private String pickupDay;
    @Schema(description = "Day of the week off for generator", example = "DOMINGO", implementation = DayWeek.class)
    @ValueOfEnum(enumClass = DayWeek.class)
    private String offDay;
    @Schema(description = "Generator opening time", example = "09:00:00", type = "string", format = "time")
    private LocalTime openingTime;
    @Schema(description = "Generator closing time", example = "20:00:00", type = "string", format = "time")
    private LocalTime closingTime;

    @Schema(description = "Generator email", example = "example@gmail.com")
    @Column(nullable = false)
    @Email
    @NotBlank
    private String email;
    @Schema(description = "Generator address", example = "C. Tulipán")
    @Column(unique = true, nullable = false)
    @NotBlank
    private String address;
    @ArraySchema(schema = @Schema(description = "Phone number", example = "123456789", implementation = String.class),
        arraySchema = @Schema(description = "List of generator phone numbers", example = "[\"123456789\", \"987654321\"]"))
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    @UniqueElements
    @NotEmpty
    private List<@Pattern(regexp = "\\d{9}") String> phoneNumber;
    @Schema(description = "Additional observations on the generator", example = "Additional observations")
    private String observations;

    public Generator() {
        //Default empty constructor
    }

    public Generator(String name, @NotNull Society society, @NotNull GeneratorType generatorType, boolean active, @NotNull LocalDate initializationDate, LocalDate terminationDate, int periodicity, String pickupDay, String offDay, LocalTime openingTime, LocalTime closingTime, String email, String address, List<@Pattern(regexp = "\\d{9}") String> phoneNumber, String observations) {
        this.name = name;
        this.society = society;
        this.generatorType = generatorType;
        this.active = active;
        this.initializationDate = initializationDate;
        this.terminationDate = terminationDate;
        this.periodicity = periodicity;
        this.pickupDay = pickupDay;
        this.offDay = offDay;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.observations = observations;
    }

    public Generator(long id, String name, @NotNull Society society, @NotNull GeneratorType generatorType, boolean active, @NotNull LocalDate initializationDate, LocalDate terminationDate, int periodicity, String pickupDay, String offDay, LocalTime openingTime, LocalTime closingTime, String email, String address, List<@Pattern(regexp = "\\d{9}") String> phoneNumber, String observations) {
        this(name, society, generatorType, active, initializationDate, terminationDate, periodicity, pickupDay, offDay, openingTime, closingTime, email, address, phoneNumber, observations);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @NotNull Society getSociety() {
        return society;
    }

    public void setSociety(@NotNull Society society) {
        this.society = society;
    }

    public @NotNull GeneratorType getGeneratorType() {
        return generatorType;
    }

    public void setGeneratorType(@NotNull GeneratorType generatorType) {
        this.generatorType = generatorType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public @NotNull LocalDate getInitializationDate() {
        return initializationDate;
    }

    public void setInitializationDate(@NotNull LocalDate initializationDate) {
        this.initializationDate = initializationDate;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public int getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(int periodicity) {
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

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
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

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Generator generator)) return false;
        return id == generator.id && active == generator.active && periodicity == generator.periodicity && Objects.equals(name, generator.name) && Objects.equals(society, generator.society) && Objects.equals(generatorType, generator.generatorType) && Objects.equals(initializationDate, generator.initializationDate) && Objects.equals(terminationDate, generator.terminationDate) && Objects.equals(pickupDay, generator.pickupDay) && Objects.equals(offDay, generator.offDay) && Objects.equals(openingTime, generator.openingTime) && Objects.equals(closingTime, generator.closingTime) && Objects.equals(email, generator.email) && Objects.equals(address, generator.address) && new HashSet<>(phoneNumber).equals(new HashSet<>(generator.phoneNumber)) && Objects.equals(observations, generator.observations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, society, generatorType, active, initializationDate, terminationDate, periodicity, pickupDay, offDay, openingTime, closingTime, email, address, phoneNumber, observations);
    }
}