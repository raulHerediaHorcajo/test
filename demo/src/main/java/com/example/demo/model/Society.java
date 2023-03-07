package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

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
}