package com.epicode.NearMissHub.payloads.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileImageRequest {

    @NotBlank(message = "profileImageUrl must not be blank")
    public String profileImageUrl;
}
