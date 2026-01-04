package com.epicode.NearMissHub.payloads.response;

import com.epicode.NearMissHub.entities.Role;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMeResponse {
    public UUID id;
    public String email;
    public String name;
    public String surname;
    public Role role;
    public String profileImageUrl;
    public LocalDateTime registrationDate;

    public UserMeResponse(UUID id, String email, String name, String surname, Role role, String profileImageUrl, LocalDateTime registrationDate) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.registrationDate = registrationDate;
    }
}
