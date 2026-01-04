package com.epicode.NearMissHub.payloads.request;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateAssignmentRequest {

    @NotNull(message = "assignedToUserId must not be null")
    public UUID assignedToUserId;

    public String note;
}
