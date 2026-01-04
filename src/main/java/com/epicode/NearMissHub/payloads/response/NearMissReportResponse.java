package com.epicode.NearMissHub.payloads.response;

import com.epicode.NearMissHub.entities.ReportArea;
import com.epicode.NearMissHub.entities.ReportStatus;
import com.epicode.NearMissHub.entities.RiskLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public class NearMissReportResponse {
    public UUID id;
    public String title;
    public String description;
    public String location;
    public ReportArea area;
    public RiskLevel riskLevel;
    public ReportStatus status;
    public LocalDateTime createdAt;
    public UserSummaryResponse createdBy;
    public CategorySummaryResponse category;

    public NearMissReportResponse(UUID id,
                                  String title,
                                  String description,
                                  String location,
                                  ReportArea area,
                                  RiskLevel riskLevel,
                                  ReportStatus status,
                                  LocalDateTime createdAt,
                                  UserSummaryResponse createdBy,
                                  CategorySummaryResponse category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.area = area;
        this.riskLevel = riskLevel;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.category = category;
    }
}
