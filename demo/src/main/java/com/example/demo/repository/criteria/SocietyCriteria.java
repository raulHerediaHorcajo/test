package com.example.demo.repository.criteria;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

public class SocietyCriteria {

    @Schema(description = "CIF or DNI of the requested Society", example = "XXXXXXXXXX")
    private String cifDni;
    @Schema(description = "Name of the requested Society", example = "Example name")
    private String name;

    public SocietyCriteria(String cifDni, String name) {
        this.cifDni = cifDni;
        this.name = name;
    }

    public String getCifDni() {
        return cifDni;
    }

    public void setCifDni(String cifDni) {
        this.cifDni = cifDni;
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
        if (!(o instanceof SocietyCriteria that)) return false;
        return Objects.equals(cifDni, that.cifDni) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cifDni, name);
    }
}
