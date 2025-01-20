package com.phanta.waladom.config;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String error;
    private String message;
    private int status;
    private LocalDateTime timestamp; // To store the time of the error
    private String path; // To store the path of the request

    // Constructor
    public ErrorResponse(String error, String message, int status, LocalDateTime timestamp, String path) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
    }

    // Getters and setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
