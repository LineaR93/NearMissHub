package com.epicode.NearMissHub.payloads.response;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

import java.util.UUID;

public class CategorySummaryResponse {
    public UUID id;
    public String name;

    public CategorySummaryResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
