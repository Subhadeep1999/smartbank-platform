package com.smartbank.auth.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "banks",
        schema = "auth_schema",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bank_bank_id",
                        columnNames = "bank_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_bank_bank_id",
                        columnList = "bank_id"
                ),
                @Index(
                        name = "idx_bank_status",
                        columnList = "status"
                )
        }
)
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "bank_id",
            nullable = false,
            length = 20
    )
    private String bankId;

    @Column(
            name = "bank_name",
            nullable = false,
            length = 150
    )
    private String bankName;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private BankStatus status;

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

    protected Bank() {
        // Required by JPA
    }

    public Bank(String bankId, String bankName) {
        this.bankId = bankId;
        this.bankName = bankName;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = BankStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public BankStatus getStatus() {
        return status;
    }

    public void setStatus(BankStatus status) {
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