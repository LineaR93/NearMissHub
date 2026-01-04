package com.epicode.NearMissHub.services;

// Service layer: business rules live here so controllers stay thin and readable.

import com.epicode.NearMissHub.entities.Role;
import com.epicode.NearMissHub.entities.User;
import com.epicode.NearMissHub.exceptions.BadRequestException;
import com.epicode.NearMissHub.exceptions.ResourceNotFoundException;
import com.epicode.NearMissHub.payloads.request.ChangePasswordRequest;
import com.epicode.NearMissHub.payloads.request.CreateUserRequest;
import com.epicode.NearMissHub.payloads.request.UpdateMeRequest;
import com.epicode.NearMissHub.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository users;
    private final CloudinaryService cloudinary;

    public UserService(PasswordEncoder passwordEncoder, UserRepository users, CloudinaryService cloudinary) {
        this.passwordEncoder = passwordEncoder;
        this.users = users;
        this.cloudinary = cloudinary;
    }

    public User create(CreateUserRequest body) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setEmail(body.email);
        u.setPassword(passwordEncoder.encode(body.password));
        u.setName(body.name);
        u.setSurname(body.surname);
        u.setRegistrationDate(LocalDateTime.now());
        u.setProfileImageUrl(null);
        // Bootstrap rule for Postman/exam: the first user ever registered becomes VALIDATOR.
        // This avoids DB seeding and lets you immediately manage roles (e.g. promote a TRIAGER).
        // Every other user starts as REPORTER.
        Role initialRole = (users.count() == 0) ? Role.VALIDATOR : Role.REPORTER;
        u.setRole(initialRole);

        return users.save(u);
    }

    public List<User> listAll() {
        return users.findAll();
    }

    public User getById(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateMe(UUID userId, UpdateMeRequest body) {
        User u = getById(userId);

        if (body.email != null) {
            String email = body.email.trim();
            if (email.isBlank()) throw new BadRequestException("email must not be blank");
            users.findByEmail(email).ifPresent(existing -> {
                if (!existing.getId().equals(u.getId())) {
                    throw new BadRequestException("Email already in use");
                }
            });
            u.setEmail(email);
        }
        if (body.name != null) {
            String name = body.name.trim();
            if (name.isBlank()) throw new BadRequestException("name must not be blank");
            u.setName(name);
        }
        if (body.surname != null) {
            String surname = body.surname.trim();
            if (surname.isBlank()) throw new BadRequestException("surname must not be blank");
            u.setSurname(surname);
        }

        return users.save(u);
    }

    public void changePassword(UUID userId, ChangePasswordRequest body) {
        User u = getById(userId);
        if (!passwordEncoder.matches(body.currentPassword, u.getPassword())) {
            throw new BadRequestException("Current password is invalid");
        }
        if (body.newPassword == null || body.newPassword.isBlank()) {
            throw new BadRequestException("newPassword must not be blank");
        }
        u.setPassword(passwordEncoder.encode(body.newPassword));
        users.save(u);
    }

    public User updateProfileImage(UUID userId, String url) {
        User u = getById(userId);
        u.setProfileImageUrl(url);
        return users.save(u);
    }

    public User updateProfileImageUpload(UUID userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("file must be provided");
        }
        String url = cloudinary.upload(file);
        return updateProfileImage(userId, url);
    }

    public User setRole(UUID userId, Role role) {
        User u = getById(userId);
        u.setRole(role);
        return users.save(u);
    }
}
