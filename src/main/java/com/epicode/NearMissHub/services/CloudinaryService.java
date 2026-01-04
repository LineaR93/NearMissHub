package com.epicode.NearMissHub.services;

// Service layer: business rules live here so controllers stay thin and readable.

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file) {
        try {
            Map<?, ?> res = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) res.get("secure_url");
        } catch (Exception e) {
            throw new com.epicode.NearMissHub.exceptions.ExternalServiceException("Cloudinary", "Upload failed", e);
        }
    }
}
