package com.smartbank.auth.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "role_permissions",
        schema = "auth_schema",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_role_permission",
                        columnNames = {"role_id", "permission_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_role_permission_role_id",
                        columnList = "role_id"
                ),
                @Index(
                        name = "idx_role_permission_permission_id",
                        columnList = "permission_id"
                ),
                @Index(
                        name = "idx_role_permission_status",
                        columnList = "status"
                )
        }
)
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "role_id",
            nullable = false
    )
    private UUID roleId;

    @Column(
            name = "permission_id",
            nullable = false
    )
    private UUID permissionId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private RolePermissionStatus status;

    @Column(
            name = "assigned_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime assignedAt;

    @Column(
            name = "assigned_by",
            length = 30
    )
    private String assignedBy;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    protected RolePermission() {
        // Required by JPA
    }

    public RolePermission(
            UUID roleId,
            UUID permissionId,
            String assignedBy
    ) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.assignedBy = assignedBy;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        assignedAt = now;
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = RolePermissionStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(UUID permissionId) {
        this.permissionId = permissionId;
    }

    public RolePermissionStatus getStatus() {
        return status;
    }

    public void setStatus(RolePermissionStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }
}