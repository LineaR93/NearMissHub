package com.epicode.NearMissHub.payloads.request;

import com.epicode.NearMissHub.entities.ReportStatus;
import jakarta.validation.constraints.NotNull;

public class ChangeStatusRequest {

    @NotNull(message = "toStatus must not be null")
    public ReportStatus toStatus;

    public String note;
}
