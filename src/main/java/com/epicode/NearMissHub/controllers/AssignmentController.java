package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services, and returns DTOs.

import com.epicode.NearMissHub.payloads.request.CreateAssignmentRequest;
import com.epicode.NearMissHub.payloads.response.AssignmentResponse;
import com.epicode.NearMissHub.payloads.response.UserSummaryResponse;
import com.epicode.NearMissHub.services.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class AssignmentController {

    private final AssignmentService service;

    public AssignmentController(AssignmentService service) {
        this.service = service;
    }

    // TRIAGER/VALIDATOR only via SecurityConfig.
    @PutMapping("/reports/{id}/assignment")
    public AssignmentResponse upsert(Authentication auth,
                                    @PathVariable UUID id,
                                    @Valid @RequestBody CreateAssignmentRequest body) {
        UUID assignedById = UUID.fromString(auth.getName());
        var a = service.upsert(id, assignedById, body);
        return toResponse(a);
    }

    // Ownership-protected: REPORTER can read only own reports; VALIDATOR/TRIAGER can read all.
    @GetMapping("/reports/{id}/assignment")
    public AssignmentResponse get(Authentication auth, @PathVariable UUID id) {
        UUID userId = UUID.fromString(auth.getName());
        return toResponse(service.getByReportId(id, userId));
    }

    private AssignmentResponse toResponse(com.epicode.NearMissHub.entities.Assignment a) {
        var by = a.getAssignedBy();
        var to = a.getAssignedTo();
        return new AssignmentResponse(
                a.getId(),
                a.getAssignedAt(),
                a.getNote(),
                new UserSummaryResponse(by.getId(), by.getEmail(), by.getName(), by.getSurname(), by.getRole()),
                new UserSummaryResponse(to.getId(), to.getEmail(), to.getName(), to.getSurname(), to.getRole())
        );
    }
}
