package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services, and returns DTOs.

import com.epicode.NearMissHub.entities.NearMissReport;
import com.epicode.NearMissHub.payloads.request.CreateNearMissReportRequest;
import com.epicode.NearMissHub.payloads.response.CategorySummaryResponse;
import com.epicode.NearMissHub.payloads.response.NearMissReportResponse;
import com.epicode.NearMissHub.payloads.response.UserSummaryResponse;
import com.epicode.NearMissHub.services.NearMissReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    private final NearMissReportService service;

    public ReportsController(NearMissReportService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NearMissReportResponse> create(Authentication auth,
                                                         @Valid @RequestBody CreateNearMissReportRequest body) {
        UUID creatorId = UUID.fromString(auth.getName());
        NearMissReport created = service.create(body, creatorId);
        return new ResponseEntity<>(toResponse(created), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public NearMissReportResponse getById(Authentication auth, @PathVariable UUID id) {
        UUID userId = UUID.fromString(auth.getName());
        return toResponse(service.getById(id, userId));
    }

    @GetMapping
    public List<NearMissReportResponse> list(Authentication auth,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) UUID categoryId,
                                            @RequestParam(required = false) String sortBy,
                                            @RequestParam(required = false) String sortDir) {
        UUID userId = UUID.fromString(auth.getName());
        return service.list(userId, status, categoryId, sortBy, sortDir)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private NearMissReportResponse toResponse(NearMissReport r) {
        var u = r.getCreatedBy();
        var createdBy = new UserSummaryResponse(u.getId(), u.getEmail(), u.getName(), u.getSurname(), u.getRole());

        CategorySummaryResponse cat = null;
        if (r.getCategory() != null) {
            cat = new CategorySummaryResponse(r.getCategory().getId(), r.getCategory().getName());
        }

        return new NearMissReportResponse(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getLocation(),
                r.getArea(),
                r.getRiskLevel(),
                r.getStatus(),
                r.getCreatedAt(),
                createdBy,
                cat
        );
    }
}
