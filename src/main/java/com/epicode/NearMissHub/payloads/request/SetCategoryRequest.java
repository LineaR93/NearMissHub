package com.epicode.NearMissHub.payloads.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SetCategoryRequest {

    @NotNull(message = "categoryId must not be null")
    public UUID categoryId;
}
