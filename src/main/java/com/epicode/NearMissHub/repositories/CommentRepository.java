package com.epicode.NearMissHub.repositories;

// Spring Data repository: declarative DB access used by the service layer.

import com.epicode.NearMissHub.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByReportIdOrderByCreatedAtAsc(UUID reportId);
}
