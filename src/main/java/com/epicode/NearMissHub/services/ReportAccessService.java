package com.epicode.NearMissHub.services;

import com.epicode.NearMissHub.entities.Report;
import com.epicode.NearMissHub.entities.Role;
import com.epicode.NearMissHub.entities.User;
import com.epicode.NearMissHub.exceptions.ForbiddenException;
import com.epicode.NearMissHub.exceptions.ResourceNotFoundException;
import com.epicode.NearMissHub.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportAccessService {

    private final UserRepository users;

    public ReportAccessService(UserRepository users) {
        this.users = users;
    }

    public User requireUser(UUID userId) {
        return users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public boolean isPrivileged(User u) {
        return u.getRole() == Role.VALIDATOR || u.getRole() == Role.TRIAGER;
    }

    public void assertCanReadReport(User u, Report r) {
        if (isPrivileged(u)) return;
        if (r.getCreatedBy() == null || !r.getCreatedBy().getId().equals(u.getId())) {
            throw new ForbiddenException("You cannot access this report");
        }
    }

    public void assertCanReadReport(UUID userId, Report r) {
        User u = requireUser(userId);
        assertCanReadReport(u, r);
    }

    public void assertIsCreator(UUID userId, Report r) {
        if (r.getCreatedBy() == null || !r.getCreatedBy().getId().equals(userId)) {
            throw new ForbiddenException("Only the report creator can perform this action");
        }
    }

    public void assertCanTriage(User actor) {
        if (actor.getRole() != Role.TRIAGER && actor.getRole() != Role.VALIDATOR) {
            throw new ForbiddenException("Only TRIAGER or VALIDATOR can perform this action");
        }
    }
}
