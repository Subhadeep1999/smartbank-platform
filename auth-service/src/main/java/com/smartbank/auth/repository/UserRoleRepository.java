package com.smartbank.auth.repository;

import com.smartbank.auth.entity.UserRole;
import com.smartbank.auth.entity.UserRoleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UUID> {

    List<UserRole> findByUserIdAndStatus(
            UUID userId,
            UserRoleStatus status
    );

    boolean existsByUserIdAndRoleId(
            UUID userId,
            UUID roleId
    );
}