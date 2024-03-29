package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Schema(description = "Represents the information of a Generator Type")
@Entity
public class GeneratorType {

    @Schema(description = "Generator Type Identifier", example = "1")
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Schema(description = "Generator Type name", example = "Example name")
    @Column(unique = true, nullable = false)
    @NotBlank
    private String name;

    public GeneratorType() {
        //Default empty constructor
    }

    public GeneratorType(String name) {
        this.name = name;
    }

    public GeneratorType(long id, String name) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneratorType that)) return false;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
