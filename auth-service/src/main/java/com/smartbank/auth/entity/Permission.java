package com.smartbank.auth.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "permissions",
        schema = "auth_schema",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_permission_permission_code",
                        columnNames = "permission_code"
                )
        },
        indexes = {
                @Index(
                        name = "idx_permission_permission_code",
                        columnList = "permission_code"
                ),
                @Index(
                        name = "idx_permission_status",
                        columnList = "status"
                )
        }
)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "permission_code",
            nullable = false,
            length = 50
    )
    private String permissionCode;

    @Column(
            name = "permission_name",
            nullable = false,
            length = 100
    )
    private String permissionName;

    @Column(
            name = "description",
            length = 255
    )
    private String description;

    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private String status;

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

    protected Permission() {
        // Required by JPA
    }

    public Permission(
            String permissionCode,
            String permissionName,
            String description
    ) {
        this.permissionCode = permissionCode;
        this.permissionName = permissionName;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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