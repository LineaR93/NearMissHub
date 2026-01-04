package com.epicode.NearMissHub.services;

import com.epicode.NearMissHub.entities.Attachment;
import com.epicode.NearMissHub.exceptions.BadRequestException;
import com.epicode.NearMissHub.exceptions.ResourceNotFoundException;
import com.epicode.NearMissHub.repositories.AttachmentRepository;
import com.epicode.NearMissHub.repositories.ReportRepository;
import com.epicode.NearMissHub.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachments;
    private final ReportRepository reports;
    private final UserRepository users;
    private final CloudinaryService cloudinary;
    private final ReportAccessService access;

    public AttachmentService(AttachmentRepository attachments,
                             ReportRepository reports,
                             UserRepository users,
                             CloudinaryService cloudinary,
                             ReportAccessService access) {
        this.attachments = attachments;
        this.reports = reports;
        this.users = users;
        this.cloudinary = cloudinary;
        this.access = access;
    }

    public Attachment upload(UUID reportId, UUID uploaderId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("file must be provided");
        }

        var report = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        var uploader = access.requireUser(uploaderId);
        access.assertCanReadReport(uploader, report);

        String url = cloudinary.upload(file);

        Attachment a = new Attachment();
        a.setId(UUID.randomUUID());
        a.setReport(report);
        a.setUploadedBy(uploader);
        a.setUrl(url);
        a.setFileName(file.getOriginalFilename());
        a.setContentType(file.getContentType());
        a.setSizeBytes(String.valueOf(file.getSize()));
        a.setUploadedAt(LocalDateTime.now());

        return attachments.save(a);
    }

    public List<Attachment> listByReport(UUID reportId, UUID currentUserId) {
        var report = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        var current = access.requireUser(currentUserId);
        access.assertCanReadReport(current, report);

        return attachments.findByReportIdOrderByUploadedAtDesc(reportId);
    }

    public void delete(UUID reportId, UUID attachmentId, UUID currentUserId) {
        var report = reports.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        var current = access.requireUser(currentUserId);
        access.assertCanReadReport(current, report);

        var a = attachments.findByIdAndReportId(attachmentId, reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        attachments.delete(a);
    }
}
