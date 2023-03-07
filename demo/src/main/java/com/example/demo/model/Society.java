package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Entity
public class Society {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    @NotBlank
    private String cifDni;
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
        if (!(o instanceof Society society)) return false;
        return id == society.id && cifDni.equals(society.cifDni) && name.equals(society.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cifDni, name);
    }
}