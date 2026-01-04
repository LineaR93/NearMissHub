package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services, and returns DTOs.

import com.epicode.NearMissHub.entities.Comment;
import com.epicode.NearMissHub.payloads.request.CreateCommentRequest;
import com.epicode.NearMissHub.payloads.response.CommentResponse;
import com.epicode.NearMissHub.payloads.response.UserSummaryResponse;
import com.epicode.NearMissHub.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping("/reports/{id}/comments")
    public CommentResponse create(Authentication auth,
                                  @PathVariable UUID id,
                                  @RequestBody @Valid CreateCommentRequest body) {

        UUID authorId = UUID.fromString(auth.getName());
        Comment saved = service.create(id, authorId, body);
        return toResponse(saved);
    }

    @GetMapping("/reports/{id}/comments")
    public List<CommentResponse> list(Authentication auth, @PathVariable UUID id) {
        UUID userId = UUID.fromString(auth.getName());
        return service.listByReport(id, userId).stream().map(this::toResponse).toList();
    }

    private CommentResponse toResponse(Comment c) {
        var u = c.getAuthor();
        var author = new UserSummaryResponse(u.getId(), u.getEmail(), u.getName(), u.getSurname(), u.getRole());
        return new CommentResponse(c.getId(), c.getText(), c.getCreatedAt(), author);
    }
}
