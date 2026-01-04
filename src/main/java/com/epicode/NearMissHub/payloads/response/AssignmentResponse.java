package com.epicode.NearMissHub.payloads.response;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

import java.time.LocalDateTime;
import java.util.UUID;

public class AssignmentResponse {
    public UUID id;
    public LocalDateTime assignedAt;
    public String note;
    public UserSummaryResponse assignedBy;
    public UserSummaryResponse assignedTo;

    public AssignmentResponse(UUID id,
                             LocalDateTime assignedAt,
                             String note,
                             UserSummaryResponse assignedBy,
                             UserSummaryResponse assignedTo) {
        this.id = id;
        this.assignedAt = assignedAt;
        this.note = note;
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
    }
}
