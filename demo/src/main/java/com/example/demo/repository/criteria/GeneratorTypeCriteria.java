package com.example.demo.repository.criteria;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

public class GeneratorTypeCriteria {

    @Schema(description = "Name of the requested Generator Type", example = "Example name")
    private String name;

    public GeneratorTypeCriteria(String name) {
        this.name = name;
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
        if (!(o instanceof GeneratorTypeCriteria that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
