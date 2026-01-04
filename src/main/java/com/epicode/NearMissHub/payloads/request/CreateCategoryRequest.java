package com.epicode.NearMissHub.payloads.request;

import com.epicode.NearMissHub.entities.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCategoryRequest {

    @NotBlank(message = "name must not be blank")
    public String name;

    public String description;

    @NotNull(message = "type must not be null")
    public CategoryType type;
}
