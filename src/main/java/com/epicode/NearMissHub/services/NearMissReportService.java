package com.epicode.NearMissHub.services;

// Service layer: business rules live here so controllers stay thin and readable.

import com.epicode.NearMissHub.entities.Category;
import com.epicode.NearMissHub.entities.NearMissReport;
import com.epicode.NearMissHub.entities.ReportStatus;
import com.epicode.NearMissHub.entities.Role;
import com.epicode.NearMissHub.entities.User;
import com.epicode.NearMissHub.exceptions.BadRequestException;
import com.epicode.NearMissHub.exceptions.ResourceNotFoundException;
import com.epicode.NearMissHub.payloads.request.CreateNearMissReportRequest;
import com.epicode.NearMissHub.repositories.CategoryRepository;
import com.epicode.NearMissHub.repositories.NearMissReportRepository;
import com.epicode.NearMissHub.repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class NearMissReportService {

    private final NearMissReportRepository reports;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final ReportAccessService access;

    public NearMissReportService(NearMissReportRepository reports,
                                 UserRepository users,
                                 CategoryRepository categories,
                                 ReportAccessService access) {
        this.reports = reports;
        this.users = users;
        this.categories = categories;
        this.access = access;
    }

    public NearMissReport create(CreateNearMissReportRequest body, UUID creatorId) {
        var creator = users.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categories.findById(body.categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        NearMissReport r = new NearMissReport();
        r.setId(UUID.randomUUID());
        r.setStatus(ReportStatus.DRAFT);
        r.setTitle(body.title);
        r.setDescription(body.description);
        r.setArea(body.area);
        r.setLocation(body.location);
        r.setCreatedAt(LocalDateTime.now());
        r.setCreatedBy(creator);
        r.setRiskLevel(body.riskLevel);
        r.setCategory(category);

        return reports.save(r);
    }

    public NearMissReport getById(UUID reportId, UUID requesterId) {
        NearMissReport r = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        User requester = access.requireUser(requesterId);
        access.assertCanReadReport(requester, r);
        return r;
    }

    public List<NearMissReport> list(UUID requesterId, String status, UUID categoryId, String sortBy, String sortDir) {
        User requester = access.requireUser(requesterId);

        String field = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;

        // Avoid runtime errors from invalid sort fields (e.g. sortBy=banana).
        Set<String> allowedSortFields = Set.of("createdAt", "status", "title", "area", "location", "riskLevel");
        if (!allowedSortFields.contains(field)) {
            throw new BadRequestException("Invalid sortBy field. Allowed: " + allowedSortFields);
        }

        Sort.Direction dir = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(dir, field);

        ReportStatus parsedStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                parsedStatus = ReportStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status. Allowed: DRAFT, SUBMITTED, IN_REVIEW, COMPLETED");
            }
        }

        // Ownership filtering: REPORTER only sees own reports.
        // TRIAGER + VALIDATOR see all.
        if (requester.getRole() == Role.REPORTER) {
            if (parsedStatus != null && categoryId != null) {
                return reports.findByCreatedByIdAndStatusAndCategoryId(requester.getId(), parsedStatus, categoryId, sort);
            }
            if (parsedStatus != null) {
                return reports.findByCreatedByIdAndStatus(requester.getId(), parsedStatus, sort);
            }
            if (categoryId != null) {
                return reports.findByCreatedByIdAndCategoryId(requester.getId(), categoryId, sort);
            }
            return reports.findByCreatedById(requester.getId(), sort);
        }

        if (parsedStatus != null && categoryId != null) {
            return reports.findByStatusAndCategoryId(parsedStatus, categoryId, sort);
        }
        if (parsedStatus != null) {
            return reports.findByStatus(parsedStatus, sort);
        }
        if (categoryId != null) {
            return reports.findByCategoryId(categoryId, sort);
        }
        return reports.findAll(sort);
    }
}
