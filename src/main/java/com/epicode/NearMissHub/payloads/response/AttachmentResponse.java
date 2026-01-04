package com.epicode.NearMissHub.payloads.response;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

import java.time.LocalDateTime;
import java.util.UUID;

public class AttachmentResponse {
    public UUID id;
    public String url;
    public String fileName;
    public String contentType;
    public String sizeBytes;
    public LocalDateTime uploadedAt;
    public UserSummaryResponse uploadedBy;

    public AttachmentResponse(UUID id,
                             String url,
                             String fileName,
                             String contentType,
                             String sizeBytes,
                             LocalDateTime uploadedAt,
                             UserSummaryResponse uploadedBy) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
    }
}
