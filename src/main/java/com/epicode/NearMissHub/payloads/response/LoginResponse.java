package com.epicode.NearMissHub.payloads.response;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

public class LoginResponse {
    public String accessToken;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
