package com.epicode.NearMissHub.entities;

// JPA entity: maps to a DB table. I use UUIDs to keep the API simple for the exam.

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_status_history")

public class ReportStatusHistory {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus toStatus;

    @ManyToOne
    @JoinColumn(name = "changed_by_user_id", nullable = false)
    private User changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @Column(length = 1000)
    private String note;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Report getReport() { return report; }
    public void setReport(Report report) { this.report = report; }

    public ReportStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(ReportStatus fromStatus) { this.fromStatus = fromStatus; }

    public ReportStatus getToStatus() { return toStatus; }
    public void setToStatus(ReportStatus toStatus) { this.toStatus = toStatus; }

    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
