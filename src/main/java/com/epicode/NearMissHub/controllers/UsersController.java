package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services, and returns DTOs.

import com.epicode.NearMissHub.payloads.request.CreateUserRequest;
import com.epicode.NearMissHub.payloads.request.UpdateRoleRequest;
import com.epicode.NearMissHub.payloads.response.UserSummaryResponse;
import com.epicode.NearMissHub.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService service;

    public UsersController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserSummaryResponse> create(@Valid @RequestBody CreateUserRequest body) {
        var created = service.create(body);
        var response = new UserSummaryResponse(
                created.getId(),
                created.getEmail(),
                created.getName(),
                created.getSurname(),
                created.getRole()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // VALIDATOR only (enforced by SecurityConfig)
    @GetMapping
    public List<UserSummaryResponse> listAll() {
        return service.listAll().stream()
                .map(u -> new UserSummaryResponse(u.getId(), u.getEmail(), u.getName(), u.getSurname(), u.getRole()))
                .toList();
    }

    // VALIDATOR only (enforced by SecurityConfig)
    @PatchMapping("/{id}/role")
    public UserSummaryResponse setRole(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest body) {
        var saved = service.setRole(id, body.role);
        return new UserSummaryResponse(saved.getId(), saved.getEmail(), saved.getName(), saved.getSurname(), saved.getRole());
    }
}
