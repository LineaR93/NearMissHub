package com.epicode.NearMissHub.payloads.response;

import java.util.UUID;

public class ReportStatusResponse {
    public UUID reportId;
    public String status;

    public ReportStatusResponse(UUID reportId, String status) {
        this.reportId = reportId;
        this.status = status;
    }
}
