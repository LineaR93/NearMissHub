package com.epicode.NearMissHub.services;

// Service layer: business rules live here so controllers stay thin and readable.

import com.epicode.NearMissHub.entities.User;
import com.epicode.NearMissHub.exceptions.UnauthorizedException;
import com.epicode.NearMissHub.repositories.UserRepository;
import com.epicode.NearMissHub.security.JwtTools;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtTools jwtTools;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtTools jwtTools) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtTools = jwtTools;
    }


    public String login(String email, String password) {
        User user = users.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return jwtTools.createToken(
                user.getId().toString(),
                user.getRole().name()
        );

    }

}
