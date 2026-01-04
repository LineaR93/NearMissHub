package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services, and returns DTOs.

import com.epicode.NearMissHub.payloads.request.ChangePasswordRequest;
import com.epicode.NearMissHub.payloads.request.UpdateMeRequest;
import com.epicode.NearMissHub.payloads.request.UpdateProfileImageRequest;
import com.epicode.NearMissHub.payloads.response.MessageResponse;
import com.epicode.NearMissHub.payloads.response.UserMeResponse;
import com.epicode.NearMissHub.services.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/me")
public class MeController {

    private final UserService users;

    public MeController(UserService users) {
        this.users = users;
    }

    @GetMapping
    public UserMeResponse me(Authentication auth) {
        UUID id = UUID.fromString(auth.getName());
        var u = users.getById(id);
        return new UserMeResponse(
                u.getId(), u.getEmail(), u.getName(), u.getSurname(),
                u.getRole(), u.getProfileImageUrl(), u.getRegistrationDate()
        );
    }

    @PatchMapping
    public UserMeResponse updateMe(Authentication auth, @RequestBody @Valid UpdateMeRequest body) {
        UUID id = UUID.fromString(auth.getName());
        var u = users.updateMe(id, body);
        return new UserMeResponse(
                u.getId(), u.getEmail(), u.getName(), u.getSurname(),
                u.getRole(), u.getProfileImageUrl(), u.getRegistrationDate()
        );
    }

    @PatchMapping("/password")
    public MessageResponse changePassword(Authentication auth, @RequestBody @Valid ChangePasswordRequest body) {
        UUID id = UUID.fromString(auth.getName());
        users.changePassword(id, body);
        return new MessageResponse("Password updated");
    }

    // Simple: upload a new image and set it as profileImageUrl.
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserMeResponse uploadProfileImage(Authentication auth, @RequestParam("file") MultipartFile file) {
        UUID id = UUID.fromString(auth.getName());
        var u = users.updateProfileImageUpload(id, file);
        return new UserMeResponse(
                u.getId(), u.getEmail(), u.getName(), u.getSurname(),
                u.getRole(), u.getProfileImageUrl(), u.getRegistrationDate()
        );
    }

    // Alternative: set a URL directly (still consistent under /me).
    @PatchMapping("/profile-image")
    public UserMeResponse updateProfileImage(Authentication auth, @Valid @RequestBody UpdateProfileImageRequest body) {
        UUID id = UUID.fromString(auth.getName());
        var u = users.updateProfileImage(id, body.profileImageUrl);
        return new UserMeResponse(
                u.getId(), u.getEmail(), u.getName(), u.getSurname(),
                u.getRole(), u.getProfileImageUrl(), u.getRegistrationDate()
        );
    }
}
