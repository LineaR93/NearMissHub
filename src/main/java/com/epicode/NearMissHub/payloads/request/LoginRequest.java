package com.epicode.NearMissHub.payloads.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "email must not be blank")
    public String email;

    @NotBlank(message = "password must not be blank")
    public String password;
}
