package com.smartbank.auth.repository;

import com.smartbank.auth.entity.RolePermission;
import com.smartbank.auth.entity.RolePermissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findByRoleIdAndStatus(
            UUID roleId,
            RolePermissionStatus status
    );

    boolean existsByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId
    );
}