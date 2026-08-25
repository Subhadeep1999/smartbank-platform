package com.smartbank.auth.repository;

import com.smartbank.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(String userId);

    boolean existsByUsername(String username);

    boolean existsByUserId(String userId);

    @Query(
            value = "SELECT nextval('auth_schema.user_id_seq')",
            nativeQuery = true
    )
    Long getNextUserIdSequence();
}