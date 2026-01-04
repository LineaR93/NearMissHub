package com.epicode.NearMissHub.payloads.request;

import com.epicode.NearMissHub.entities.ReportArea;
import com.epicode.NearMissHub.entities.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateNearMissReportRequest {

    @NotBlank(message = "title must not be blank")
    public String title;

    @NotBlank(message = "description must not be blank")
    public String description;

    @NotNull(message = "area must not be null")
    public ReportArea area;

    @NotBlank(message = "location must not be blank")
    public String location;

    @NotNull(message = "riskLevel must not be null")
    public RiskLevel riskLevel;

    @NotNull(message = "categoryId must not be null")
    public UUID categoryId;
}
