package com.example.demo.repository.criteria;

public class SocietyCriteria {

    private String cifDni;
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
}
