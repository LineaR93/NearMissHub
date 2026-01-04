package com.epicode.NearMissHub.entities;

// JPA entity: maps to a DB table. I use UUIDs to keep the API simple for the exam.

import jakarta.persistence.*;

@Entity
@Table(name = "near_miss_reports")
public class NearMissReport extends Report {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
}
