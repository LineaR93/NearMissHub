package com.epicode.NearMissHub.payloads.response;

public class ErrorDetailResponse {
    public String field;
    public String message;

    public ErrorDetailResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
