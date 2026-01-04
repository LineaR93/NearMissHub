package com.epicode.NearMissHub.payloads.response;

import java.util.UUID;

public class ReportCategoryResponse {
    public UUID reportId;
    public UUID categoryId;

    public ReportCategoryResponse(UUID reportId, UUID categoryId) {
        this.reportId = reportId;
        this.categoryId = categoryId;
    }
}
