package com.epicode.NearMissHub.services;

import com.epicode.NearMissHub.entities.Comment;
import com.epicode.NearMissHub.exceptions.ResourceNotFoundException;
import com.epicode.NearMissHub.payloads.request.CreateCommentRequest;
import com.epicode.NearMissHub.repositories.CommentRepository;
import com.epicode.NearMissHub.repositories.ReportRepository;
import com.epicode.NearMissHub.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository comments;
    private final ReportRepository reports;
    private final UserRepository users;
    private final ReportAccessService access;

    public CommentService(CommentRepository comments, ReportRepository reports, UserRepository users, ReportAccessService access) {
        this.comments = comments;
        this.reports = reports;
        this.users = users;
        this.access = access;
    }

    public Comment create(UUID reportId, UUID authorId, CreateCommentRequest body) {
        var report = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        var author = access.requireUser(authorId);
        access.assertCanReadReport(author, report);

        Comment c = new Comment();
        c.setId(UUID.randomUUID());
        c.setReport(report);
        c.setAuthor(author);
        c.setText(body.text);
        c.setCreatedAt(LocalDateTime.now());

        return comments.save(c);
    }

    public List<Comment> listByReport(UUID reportId, UUID currentUserId) {
        var report = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        var currentUser = access.requireUser(currentUserId);
        access.assertCanReadReport(currentUser, report);

        return comments.findByReportIdOrderByCreatedAtAsc(reportId);
    }
}
