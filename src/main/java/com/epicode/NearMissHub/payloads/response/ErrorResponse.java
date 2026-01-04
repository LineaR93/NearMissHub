package com.epicode.NearMissHub.payloads.response;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    public LocalDateTime timestamp;
    public int status;
    public String error;
    public String message;
    public List<ErrorDetailResponse> details;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, List<ErrorDetailResponse> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, null);
    }

    public static ErrorResponse of(int status, String error, String message, List<ErrorDetailResponse> details) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, details);
    }
}
