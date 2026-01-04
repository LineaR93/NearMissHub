package com.epicode.NearMissHub.services;

// Service layer: business rules live here so controllers stay thin and readable.

import com.epicode.NearMissHub.entities.Report;
import com.epicode.NearMissHub.entities.ReportStatus;
import com.epicode.NearMissHub.entities.ReportStatusHistory;
import com.epicode.NearMissHub.entities.Role;
import com.epicode.NearMissHub.exceptions.BadRequestException;
import com.epicode.NearMissHub.exceptions.ForbiddenException;
import com.epicode.NearMissHub.exceptions.ResourceNotFoundException;
import com.epicode.NearMissHub.payloads.request.ChangeStatusRequest;
import com.epicode.NearMissHub.repositories.AssignmentRepository;
import com.epicode.NearMissHub.repositories.CategoryRepository;
import com.epicode.NearMissHub.repositories.ReportRepository;
import com.epicode.NearMissHub.repositories.ReportStatusHistoryRepository;
import com.epicode.NearMissHub.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReportWorkflowService {

    private final ReportRepository reports;
    private final CategoryRepository categories;
    private final ReportStatusHistoryRepository history;
    private final UserRepository users;
    private final AssignmentRepository assignments;
    private final MailgunNotificationService mailgun;
    private final ReportAccessService access;

    public ReportWorkflowService(ReportRepository reports,
                                 CategoryRepository categories,
                                 ReportStatusHistoryRepository history,
                                 UserRepository users,
                                 AssignmentRepository assignments,
                                 MailgunNotificationService mailgun,
                                 ReportAccessService access) {
        this.reports = reports;
        this.categories = categories;
        this.history = history;
        this.users = users;
        this.assignments = assignments;
        this.mailgun = mailgun;
        this.access = access;
    }

    public Report setCategory(UUID reportId, UUID actorId, UUID categoryId) {
        var actor = access.requireUser(actorId);

        Report r = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        // Only TRIAGER / VALIDATOR can categorize (security also enforces this).
        access.assertCanTriage(actor);

        var c = categories.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        r.setCategory(c);
        return reports.save(r);
    }

    public Report submit(UUID reportId, UUID actorId) {
        var actor = access.requireUser(actorId);

        Report r = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        // Only the creator can submit.
        if (!r.getCreatedBy().getId().equals(actorId)) {
            throw new ForbiddenException("Only the report creator can submit it");
        }

        if (r.getStatus() != ReportStatus.DRAFT) {
            throw new BadRequestException("Report is not in DRAFT");
        }

        return changeStatusInternal(r, actorId, ReportStatus.SUBMITTED, "submit");
    }

    public Report changeStatus(UUID reportId, UUID actorId, ChangeStatusRequest body) {
        // Security restricts this to VALIDATOR, but I still keep validations here.
        access.requireUser(actorId);

        Report r = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        ReportStatus next = body.toStatus;
        validateTransition(r.getStatus(), next);

        return changeStatusInternal(r, actorId, next, body.note);
    }

    public List<ReportStatusHistory> getHistory(UUID reportId, UUID actorId) {
        var actor = access.requireUser(actorId);
        Report r = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        access.assertCanReadReport(actor, r);
        return history.findByReportIdOrderByChangedAtDesc(reportId);
    }

    private void validateTransition(ReportStatus from, ReportStatus to) {
        if (to == null) {
            throw new BadRequestException("toStatus must not be null");
        }
        if (to == from) {
            throw new BadRequestException("Status is already " + from);
        }

        // Linear workflow for the exam:
        // DRAFT -> SUBMITTED (via /submit)
        // SUBMITTED -> IN_REVIEW
        // IN_REVIEW -> COMPLETED
        if (from == ReportStatus.DRAFT) {
            throw new BadRequestException("Use /reports/{id}/submit to move DRAFT -> SUBMITTED");
        }
        if (from == ReportStatus.SUBMITTED && to == ReportStatus.IN_REVIEW) {
            return;
        }
        if (from == ReportStatus.IN_REVIEW && to == ReportStatus.COMPLETED) {
            return;
        }

        throw new BadRequestException("Invalid transition: " + from + " -> " + to);
    }

    private Report changeStatusInternal(Report r, UUID userId, ReportStatus next, String note) {
        var changer = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ReportStatus from = r.getStatus();
        r.setStatus(next);
        Report saved = reports.save(r);

        // Persist status history.
        ReportStatusHistory h = new ReportStatusHistory();
        h.setId(UUID.randomUUID());
        h.setReport(saved);
        h.setFromStatus(from);
        h.setToStatus(next);
        h.setChangedBy(changer);
        h.setChangedAt(LocalDateTime.now());
        h.setNote(note);
        history.save(h);

        // Notification rule:
        String validatorEmail = assignments.findByReportId(saved.getId())
                .map(a -> a.getAssignedTo().getEmail())
                .or(() -> users.findFirstByRole(Role.VALIDATOR).map(u -> u.getEmail()))
                .orElse(null);

        if (validatorEmail != null && !validatorEmail.isBlank()) {
            mailgun.sendEmail(
                    validatorEmail,
                    "NearMissHub - Report status changed",
                    "Report '" + saved.getTitle() + "' moved from " + from + " to " + next + ". Note: " + (note == null ? "" : note)
            );
        }

        return saved;
    }
}
