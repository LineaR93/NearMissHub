package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services, and returns DTOs.

import com.epicode.NearMissHub.entities.Attachment;
import com.epicode.NearMissHub.payloads.response.AttachmentResponse;
import com.epicode.NearMissHub.payloads.response.MessageResponse;
import com.epicode.NearMissHub.payloads.response.UserSummaryResponse;
import com.epicode.NearMissHub.services.AttachmentService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class AttachmentController {

    private final AttachmentService service;

    public AttachmentController(AttachmentService service) {
        this.service = service;
    }

    @PostMapping(value = "/reports/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentResponse upload(Authentication auth,
                                     @PathVariable UUID id,
                                     @RequestParam("file") MultipartFile file) {
        UUID uploaderId = UUID.fromString(auth.getName());
        Attachment saved = service.upload(id, uploaderId, file);
        return toResponse(saved);
    }

    @GetMapping("/reports/{id}/attachments")
    public List<AttachmentResponse> list(Authentication auth, @PathVariable UUID id) {
        UUID userId = UUID.fromString(auth.getName());
        return service.listByReport(id, userId).stream().map(this::toResponse).toList();
    }

    @DeleteMapping("/reports/{reportId}/attachments/{attachmentId}")
    public MessageResponse delete(Authentication auth,
                                  @PathVariable UUID reportId,
                                  @PathVariable UUID attachmentId) {
        UUID userId = UUID.fromString(auth.getName());
        service.delete(reportId, attachmentId, userId);
        return new MessageResponse("Attachment deleted");
    }

    private AttachmentResponse toResponse(Attachment a) {
        var u = a.getUploadedBy();
        var uploader = new UserSummaryResponse(u.getId(), u.getEmail(), u.getName(), u.getSurname(), u.getRole());
        return new AttachmentResponse(
                a.getId(),
                a.getUrl(),
                a.getFileName(),
                a.getContentType(),
                a.getSizeBytes(),
                a.getUploadedAt(),
                uploader
        );
    }
}
