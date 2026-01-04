package com.epicode.NearMissHub.payloads.request;

import jakarta.validation.constraints.NotBlank;

public class CreateUserRequest {

    @NotBlank(message = "name must not be blank")
    public String name;

    @NotBlank(message = "surname must not be blank")
    public String surname;

    @NotBlank(message = "email must not be blank")
    public String email;

    @NotBlank(message = "password must not be blank")
    public String password;
}
