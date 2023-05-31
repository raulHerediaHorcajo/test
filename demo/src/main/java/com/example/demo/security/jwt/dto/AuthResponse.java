package com.example.demo.security.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the information of an access response")
public class AuthResponse {

	@Schema(description = "Response access status code", example = "SUCCESS")
	private Status status;
	@Schema(description = "Response access message", example = "Auth successful. Tokens are created in cookie.")
	private String message;
	@Schema(description = "Response access error message")
	private String error;

	public enum Status {
		SUCCESS, FAILURE
	}

	public AuthResponse(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	public AuthResponse(Status status, String message, String error) {
		this.status = status;
		this.message = message;
		this.error = error;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
