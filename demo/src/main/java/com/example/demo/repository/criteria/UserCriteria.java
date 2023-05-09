package com.example.demo.repository.criteria;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

public class UserCriteria {

    @Schema(description = "Name of the requested User", example = "Example name")
    private String name;
    @Schema(description = "Email of the requested User", example = "example@gmail.com")
    private String email;

    public UserCriteria(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCriteria that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
