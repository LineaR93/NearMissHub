package com.epicode.NearMissHub.repositories;

// Spring Data repository: declarative DB access used by the service layer.

import com.epicode.NearMissHub.entities.NearMissReport;
import com.epicode.NearMissHub.entities.ReportStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NearMissReportRepository extends JpaRepository<NearMissReport, UUID> {
    List<NearMissReport> findByStatus(ReportStatus status);
    List<NearMissReport> findByCategoryId(UUID categoryId);
    List<NearMissReport> findByStatusAndCategoryId(ReportStatus status, UUID categoryId);

    List<NearMissReport> findByStatus(ReportStatus status, Sort sort);
    List<NearMissReport> findByCategoryId(UUID categoryId, Sort sort);
    List<NearMissReport> findByStatusAndCategoryId(ReportStatus status, UUID categoryId, Sort sort);

    

    // Ownership-filtered queries (REPORTER sees only their own reports)
    List<NearMissReport> findByCreatedById(UUID createdById, Sort sort);
    List<NearMissReport> findByCreatedByIdAndStatus(UUID createdById, ReportStatus status, Sort sort);
    List<NearMissReport> findByCreatedByIdAndCategoryId(UUID createdById, UUID categoryId, Sort sort);
    List<NearMissReport> findByCreatedByIdAndStatusAndCategoryId(UUID createdById, ReportStatus status, UUID categoryId, Sort sort);
    @Query("select r.status, count(r) from Report r group by r.status")
    List<Object[]> countByStatus();

    @Query("select c.name, count(r) from Report r join r.category c group by c.name")
    List<Object[]> countByCategoryName();
}
