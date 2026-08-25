package com.smartbank.auth.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "roles",
        schema = "auth_schema",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_role_role_code",
                        columnNames = "role_code"
                )
        },
        indexes = {
                @Index(
                        name = "idx_role_role_code",
                        columnList = "role_code"
                ),
                @Index(
                        name = "idx_role_status",
                        columnList = "status"
                )
        }
)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role_code",
            nullable = false,
            length = 30
    )
    private RoleType roleCode;

    @Column(
            name = "role_name",
            nullable = false,
            length = 100
    )
    private String roleName;

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

    protected Role() {
        // Required by JPA
    }

    public Role(
            RoleType roleCode,
            String roleName,
            String description
    ) {
        this.roleCode = roleCode;
        this.roleName = roleName;
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

    public RoleType getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(RoleType roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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