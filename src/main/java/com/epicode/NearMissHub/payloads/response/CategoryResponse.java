package com.epicode.NearMissHub.payloads.response;

import com.epicode.NearMissHub.entities.CategoryType;
import java.time.LocalDateTime;
import java.util.UUID;

public class CategoryResponse {
    public UUID id;
    public String name;
    public String description;
    public CategoryType type;
    public LocalDateTime createdAt;

    public CategoryResponse(UUID id, String name, String description, CategoryType type, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
    }
}
