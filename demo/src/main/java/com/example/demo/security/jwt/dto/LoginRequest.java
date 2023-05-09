package com.example.demo.security.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Represents the information of an access request")
public class LoginRequest {

	@Schema(description = "Login email", example = "example@gmail.com")
	@Email
	@NotBlank
	private String username;
	@Schema(description = "Login password", example = "ZXhhbXBsZSBwYXNzd29yZA==")
	@NotBlank
	private String password;

	public LoginRequest() {
	}

	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
