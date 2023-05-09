package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Objects;

@Schema(description = "Represents the information of a User")
@Entity
public class User {

    @Schema(description = "User Identifier", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Schema(description = "User name", example = "Example name")
    @NotBlank
    private String name;
    @Schema(description = "User email", example = "example@gmail.com")
    @Column(unique = true)
    @Email
    @NotBlank
    private String email;
    @Schema(description = "User password", example = "ZXhhbXBsZSBwYXNzd29yZA==")
    @NotBlank
    private String password;
    @ArraySchema(schema = @Schema(description = "Role name", example = "ADMIN"),
                arraySchema = @Schema(description = "List of user roles", example = "[\"ADMIN\", \"USER\"]"))
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    public User() {
        //Default empty constructor
    }

    public User(String name, String email, String password, List<String> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        if (!(o instanceof User user)) return false;
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, roles);
    }
}
