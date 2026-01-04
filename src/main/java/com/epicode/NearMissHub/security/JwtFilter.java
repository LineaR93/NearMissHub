package com.epicode.NearMissHub.security;

// JWT-based security (stateless). JwtFilter reads the token and sets the SecurityContext.

import com.epicode.NearMissHub.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTools jwtTools;
    private final UserRepository userRepository;

    public JwtFilter(JwtTools jwtTools, UserRepository userRepository) {
        this.jwtTools = jwtTools;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // I never log the raw token. It is sensitive and would end up in console logs.

            try {
                Claims claims = jwtTools.verifyAndGetClaims(token);

                String userId = claims.getSubject();

                // IMPORTANT: role changes are handled via PATCH in Postman.
                // To make role changes effective immediately, we read the current role from the DB
                // instead of trusting the (possibly stale) "role" claim in the JWT.
                var userOpt = userRepository.findById(UUID.fromString(userId));
                if (userOpt.isEmpty()) {
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                String role = userOpt.get().getRole().name();

                // Spring Security "role" convention: ROLE_*
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                var authentication = new UsernamePasswordAuthenticationToken(
                        userId, // I store the userId as principal to keep controllers simple.
                        null,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception ex) {
                // Invalid token -> no authentication in the SecurityContext.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
