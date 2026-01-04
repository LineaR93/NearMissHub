package com.epicode.NearMissHub.entities;

// JPA entity: maps to a DB table. I use UUIDs to keep the API simple for the exam.

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attachments")

public class Attachment {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_user", nullable = false)
    private User uploadedBy;

    @Column
    private String url;

    @Column
    private String contentType;

    @Column
    private String fileName;

    @Column
    private String sizeBytes;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    public void setReport(Report report) {
        this.report = report;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    // Getters matter for JSON serialization. Without them, Postman often shows an empty {}.
    public UUID getId() { return id; }
    public Report getReport() { return report; }
    public User getUploadedBy() { return uploadedBy; }
    public String getUrl() { return url; }
    public String getContentType() { return contentType; }
    public String getFileName() { return fileName; }
    public String getSizeBytes() { return sizeBytes; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public void setSizeBytes(String sizeBytes) { this.sizeBytes = sizeBytes; }
}
