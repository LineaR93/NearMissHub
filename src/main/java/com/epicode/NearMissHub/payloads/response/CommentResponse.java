package com.epicode.NearMissHub.payloads.response;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentResponse {
    public UUID id;
    public String text;
    public LocalDateTime createdAt;
    public UserSummaryResponse author;

    public CommentResponse(UUID id, String text, LocalDateTime createdAt, UserSummaryResponse author) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.author = author;
    }
}
