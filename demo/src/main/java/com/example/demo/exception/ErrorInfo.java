package com.example.demo.exception;

public class ErrorInfo {

    private final int statusCode;
    private final String message;
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
