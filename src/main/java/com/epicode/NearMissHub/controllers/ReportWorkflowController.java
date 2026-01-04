package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services, and returns DTOs.

import com.epicode.NearMissHub.payloads.request.ChangeStatusRequest;
import com.epicode.NearMissHub.payloads.request.SetCategoryRequest;
import com.epicode.NearMissHub.payloads.response.ReportCategoryResponse;
import com.epicode.NearMissHub.payloads.response.ReportStatusResponse;
import com.epicode.NearMissHub.payloads.response.StatusHistoryResponse;
import com.epicode.NearMissHub.payloads.response.UserSummaryResponse;
import com.epicode.NearMissHub.services.ReportWorkflowService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ReportWorkflowController {

    private final ReportWorkflowService service;

    public ReportWorkflowController(ReportWorkflowService service) {
        this.service = service;
    }

    @PostMapping("/reports/{id}/submit")
    public ReportStatusResponse submit(Authentication auth, @PathVariable UUID id) {
        UUID userId = UUID.fromString(auth.getName());
        var r = service.submit(id, userId);
        return new ReportStatusResponse(r.getId(), r.getStatus().name());
    }

    @PatchMapping("/reports/{id}/status")
    public ReportStatusResponse changeStatus(Authentication auth,
                                             @PathVariable UUID id,
                                             @Valid @RequestBody ChangeStatusRequest body) {
        UUID userId = UUID.fromString(auth.getName());
        var r = service.changeStatus(id, userId, body);
        return new ReportStatusResponse(r.getId(), r.getStatus().name());
    }

    @PatchMapping("/reports/{id}/category")
    public ReportCategoryResponse setCategory(Authentication auth, @PathVariable UUID id, @Valid @RequestBody SetCategoryRequest body) {
        UUID userId = UUID.fromString(auth.getName());
        var r = service.setCategory(id, userId, body.categoryId);
        return new ReportCategoryResponse(r.getId(), r.getCategory() != null ? r.getCategory().getId() : null);
    }

    @GetMapping("/reports/{id}/status-history")
    public List<StatusHistoryResponse> history(Authentication auth, @PathVariable UUID id) {
        UUID userId = UUID.fromString(auth.getName());
        return service.getHistory(id, userId).stream()
                .map(h -> {
                    var u = h.getChangedBy();
                    var changedBy = new UserSummaryResponse(u.getId(), u.getEmail(), u.getName(), u.getSurname(), u.getRole());
                    return new StatusHistoryResponse(h.getId(), h.getFromStatus().name(), h.getToStatus().name(), h.getChangedAt(), h.getNote(), changedBy);
                })
                .toList();
    }
}
