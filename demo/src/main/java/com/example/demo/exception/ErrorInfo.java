package com.example.demo.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the information of an error response")
public class ErrorInfo {

    @Schema(description = "Response error status code", example = "400", requiredMode = Schema.RequiredMode.REQUIRED)
    private final int statusCode;
    @Schema(description = "Response error message", example = "Example error message", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String message;
    @Schema(description = "Uri requested that produced the error response", example = "/uri/example", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String uriRequested;

    public ErrorInfo(int statusCode, String message, String uriRequested) {
        this.statusCode = statusCode;
        this.message = message;
        this.uriRequested = uriRequested;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getUriRequested() {
        return uriRequested;
    }
}
