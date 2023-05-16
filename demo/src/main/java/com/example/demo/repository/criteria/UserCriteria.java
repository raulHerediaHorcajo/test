package com.example.demo.repository.criteria;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

public class UserCriteria {

    @Schema(description = "Name of the requested User", example = "Example name")
    private String name;
    @Schema(description = "Email of the requested User", example = "example@gmail.com")
    private String email;
    @ArraySchema(schema = @Schema(description = "Role name", example = "ADMIN"),
        arraySchema = @Schema(description = "List of roles of the requested User", example = "[\"ADMIN\", \"USER\"]"))
    private List<String> roles;

    public UserCriteria(String name, String email, List<String> roles) {
        this.name = name;
        this.email = email;
        this.roles = roles;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCriteria that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, roles);
    }
}
