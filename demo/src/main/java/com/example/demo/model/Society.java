package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Schema(description = "Represents the information of a Society")
@Entity
public class Society {

    @Schema(description = "Society Identifier", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Schema(description = "CIF or DNI of the Society", example = "XXXXXXXXXX")
    @Column(unique = true)
    @NotBlank
    private String cifDni;
    @Schema(description = "Society name", example = "Example name")
    @Column(unique = true)
    @NotBlank
    private String name;

    public Society() {
        //Default empty constructor
    }

    public Society(String cifDni, String name) {
        this.cifDni = cifDni;
        this.name = name;
    }

    public Society(long id, String cifDni, String name) {
        this.id = id;
        this.cifDni = cifDni;
        this.name = name;
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

    public String getCifDni() {
        return cifDni;
    }

    public void setCifDni(String cifDni) {
        this.cifDni = cifDni;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Society society)) return false;
        return id == society.id && Objects.equals(cifDni, society.cifDni) && Objects.equals(name, society.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cifDni, name);
    }
}