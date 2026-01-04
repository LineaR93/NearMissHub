package com.epicode.NearMissHub.entities;

// JPA entity: maps to a DB table. I use UUIDs to keep the API simple for the exam.

public enum ReportStatus {
    DRAFT,
    SUBMITTED,
    IN_REVIEW,
    COMPLETED
}
