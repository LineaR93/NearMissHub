package com.epicode.NearMissHub.payloads.response;

import com.epicode.NearMissHub.entities.Role;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

import java.util.UUID;

// DTO used to avoid exposing the password and to keep API responses stable if entities change.
public class UserSummaryResponse {
    public UUID id;
    public String email;
    public String name;
    public String surname;
    public Role role;

    public UserSummaryResponse(UUID id, String email, String name, String surname, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }
}
