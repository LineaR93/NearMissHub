package com.epicode.NearMissHub.payloads.request;

import com.epicode.NearMissHub.entities.Role;
import jakarta.validation.constraints.NotNull;

public class UpdateRoleRequest {

    @NotNull(message = "role must not be null")
    public Role role;
}
