package com.epicode.NearMissHub.repositories;

// Spring Data repository: declarative DB access used by the service layer.

import com.epicode.NearMissHub.entities.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByReportIdOrderByUploadedAtDesc(UUID reportId);
    java.util.Optional<Attachment> findByIdAndReportId(UUID id, UUID reportId);
}
