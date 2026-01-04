package com.epicode.NearMissHub.repositories;

// Spring Data repository: declarative DB access used by the service layer.

import com.epicode.NearMissHub.entities.User;
import com.epicode.NearMissHub.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    // Used to find the "main" validator in a predictable way for the exam demo.
    Optional<User> findFirstByRole(Role role);
}
