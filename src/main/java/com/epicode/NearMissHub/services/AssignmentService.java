package com.epicode.NearMissHub.services;

// Service layer: business rules live here so controllers stay thin and readable.

import com.epicode.NearMissHub.entities.Assignment;
import com.epicode.NearMissHub.entities.Role;
import com.epicode.NearMissHub.exceptions.ForbiddenException;
import com.epicode.NearMissHub.exceptions.ResourceNotFoundException;
import com.epicode.NearMissHub.payloads.request.CreateAssignmentRequest;
import com.epicode.NearMissHub.repositories.AssignmentRepository;
import com.epicode.NearMissHub.repositories.ReportRepository;
import com.epicode.NearMissHub.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AssignmentService {

    private final AssignmentRepository assignments;
    private final ReportRepository reports;
    private final UserRepository users;
    private final ReportAccessService access;
    private final MailgunNotificationService mailgun;

    public AssignmentService(AssignmentRepository assignments,
                             ReportRepository reports,
                             UserRepository users,
                             ReportAccessService access,
                             MailgunNotificationService mailgun) {
        this.assignments = assignments;
        this.reports = reports;
        this.users = users;
        this.access = access;
        this.mailgun = mailgun;
    }

    public Assignment upsert(UUID reportId, UUID assignedById, CreateAssignmentRequest body) {
        var report = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        var assignedBy = access.requireUser(assignedById);
        if (assignedBy.getRole() != Role.TRIAGER && assignedBy.getRole() != Role.VALIDATOR) {
            throw new ForbiddenException("Only TRIAGER or VALIDATOR can assign reports");
        }

        var assignedTo = users.findById(body.assignedToUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // One report has at most one active assignment in this simple model.
        // If it exists I update it, otherwise I create a new record.
        Assignment a = assignments.findByReportId(reportId).orElseGet(() -> {
            Assignment x = new Assignment();
            x.setId(UUID.randomUUID());
            x.setReport(report);
            return x;
        });

        a.setAssignedBy(assignedBy);
        a.setAssignedTo(assignedTo);
        a.setAssignedAt(LocalDateTime.now());
        a.setNote(body.note);

        Assignment saved = assignments.save(a);

        mailgun.sendEmail(
                assignedTo.getEmail(),
                "New assignment",
                "You have been assigned to report: " + report.getTitle()
        );

        return saved;
    }

    public Assignment getByReportId(UUID reportId, UUID currentUserId) {
        var report = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        access.assertCanReadReport(currentUserId, report);

        return assignments.findByReportId(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
    }
}
