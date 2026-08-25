package com.smartbank.auth.repository;

import com.smartbank.auth.entity.Role;
import com.smartbank.auth.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleCode(RoleType roleCode);

    boolean existsByRoleCode(RoleType roleCode);
}
