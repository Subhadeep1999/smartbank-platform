package com.smartbank.auth.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        schema = "auth_schema",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_user_id",
                        columnNames = "user_id"
                ),
                @UniqueConstraint(
                        name = "uk_user_username",
                        columnNames = "username"
                )
        },
        indexes = {
                @Index(
                        name = "idx_user_bank_id",
                        columnList = "bank_id"
                ),
                @Index(
                        name = "idx_user_customer_cif",
                        columnList = "customer_cif"
                ),
                @Index(
                        name = "idx_user_status",
                        columnList = "status"
                )
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "user_id",
            nullable = false,
            length = 30
    )
    private String userId;

    @Column(
            name = "username",
            nullable = false,
            length = 100
    )
    private String username;

    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;

    @Column(
            name = "bank_id",
            nullable = false,
            length = 20
    )
    private String bankId;

    /**
     * CIF is populated for CUSTOMER users.
     * It remains null for employees such as TELLER or ADMIN.
     */
    @Column(
            name = "customer_cif",
            length = 30
    )
    private String customerCif;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private UserStatus status;

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

    protected User() {
        // Required by JPA
    }

    public User(
            String userId,
            String username,
            String passwordHash,
            String bankId,
            String customerCif
    ) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.bankId = bankId;
        this.customerCif = customerCif;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = UserStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getCustomerCif() {
        return customerCif;
    }

    public void setCustomerCif(String customerCif) {
        this.customerCif = customerCif;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
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