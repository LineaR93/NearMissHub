package com.epicode.NearMissHub.payloads.response;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

import java.time.LocalDateTime;
import java.util.UUID;

public class StatusHistoryResponse {
    public UUID id;
    public String fromStatus;
    public String toStatus;
    public LocalDateTime changedAt;
    public String note;
    public UserSummaryResponse changedBy;

    public StatusHistoryResponse(UUID id,
                                String fromStatus,
                                String toStatus,
                                LocalDateTime changedAt,
                                String note,
                                UserSummaryResponse changedBy) {
        this.id = id;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedAt = changedAt;
        this.note = note;
        this.changedBy = changedBy;
    }
}
