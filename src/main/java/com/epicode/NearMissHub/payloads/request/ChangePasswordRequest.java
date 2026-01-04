package com.epicode.NearMissHub.payloads.request;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {
    @NotBlank(message = "currentPassword must not be blank")
    public String currentPassword;

    @NotBlank(message = "newPassword must not be blank")
    public String newPassword;
}
