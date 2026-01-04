package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services/repositories, and returns DTOs.

import com.epicode.NearMissHub.payloads.request.LoginRequest;
import com.epicode.NearMissHub.payloads.response.LoginResponse;
import com.epicode.NearMissHub.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest body) {
        String token = auth.login(body.email, body.password);
        return new LoginResponse(token);
    }

}
