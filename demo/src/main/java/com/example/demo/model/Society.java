package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Society {

    @Id
    @NotBlank
    private String cifDni;
    @Column(unique=true)
    @NotBlank
    private String name;

    public Society() {
        //Default empty constructor
    }

    public Society(String cifDni, String name) {
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
}
