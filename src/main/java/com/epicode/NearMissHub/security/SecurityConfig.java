package com.epicode.NearMissHub.security;

// JWT-based security (stateless). JwtFilter reads the token and sets the SecurityContext.

import tools.jackson.databind.ObjectMapper;
import com.epicode.NearMissHub.payloads.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter, ObjectMapper mapper) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> {
                    writeError(res, mapper, HttpStatus.UNAUTHORIZED, "Unauthorized", "Missing or invalid token");
                })
                .accessDeniedHandler((req, res, denEx) -> {
                    writeError(res, mapper, HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to perform this action");
                })
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()

                // Registration is public.
                .requestMatchers(HttpMethod.POST, "/users").permitAll()

                // User management - only VALIDATOR can list and update roles.
                .requestMatchers(HttpMethod.GET, "/users").hasRole("VALIDATOR")
                .requestMatchers(HttpMethod.PATCH, "/users/*/role").hasRole("VALIDATOR")

                // Triage / validation operations
                .requestMatchers(HttpMethod.POST, "/categories").hasAnyRole("TRIAGER", "VALIDATOR")
                .requestMatchers(HttpMethod.PATCH, "/reports/*/category").hasAnyRole("TRIAGER", "VALIDATOR")
                .requestMatchers(HttpMethod.PUT, "/reports/*/assignment").hasAnyRole("TRIAGER", "VALIDATOR")

                // Report status changes
                .requestMatchers(HttpMethod.PATCH, "/reports/*/status").hasRole("VALIDATOR")

                // KPI endpoints
                .requestMatchers(HttpMethod.GET, "/kpi/**").hasRole("VALIDATOR")

                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void writeError(HttpServletResponse res, ObjectMapper mapper, HttpStatus status, String error, String message) throws IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var body = new ErrorResponse(LocalDateTime.now(), status.value(), error, message, null);
        mapper.writeValue(res.getOutputStream(), body);
    }
}
