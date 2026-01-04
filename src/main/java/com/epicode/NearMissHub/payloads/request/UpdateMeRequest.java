package com.epicode.NearMissHub.payloads.request;

import jakarta.validation.constraints.Email;

// Optional fields: if provided, they are validated/checked in the service.
public class UpdateMeRequest {
    @Email(message = "email must be a valid email")
    public String email;

    public String name;
    public String surname;
}
