package com.epicode.NearMissHub.repositories;

// Spring Data repository: declarative DB access used by the service layer.

import com.epicode.NearMissHub.entities.ReportStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportStatusHistoryRepository extends JpaRepository<ReportStatusHistory, UUID> {
    List<ReportStatusHistory> findByReportIdOrderByChangedAtDesc(UUID reportId);
}
