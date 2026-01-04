package com.epicode.NearMissHub.repositories;

// Spring Data repository: declarative DB access used by the service layer.

import com.epicode.NearMissHub.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    Optional<Assignment> findByReportId(UUID reportId);
}
