package com.epicode.NearMissHub.payloads.request;

import jakarta.validation.constraints.NotBlank;

public class CreateCommentRequest {

    @NotBlank(message = "text must not be blank")
    public String text;
}
