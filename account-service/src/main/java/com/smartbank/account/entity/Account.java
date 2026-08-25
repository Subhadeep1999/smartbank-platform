package com.smartbank.account.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "accounts",
        schema = "account_schema",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_number",
                        columnNames = "account_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_account_number",
                        columnList = "account_number"
                ),
                @Index(
                        name = "idx_account_cif_id",
                        columnList = "cif_id"
                ),
                @Index(
                        name = "idx_account_status",
                        columnList = "status"
                )
        }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "account_number",
            nullable = false,
            unique = true,
            length = 20
    )
    private String accountNumber;

    /*
     * Cross-service customer identifier.
     *
     * CIF belongs to Customer Service.
     * Account Service stores it as a business reference.
     *
     * No @ManyToOne / foreign key is used because
     * Customer is owned by another microservice.
     */
    @Column(
            name = "cif_id",
            nullable = false,
            length = 30
    )
    private String cifId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "account_type",
            nullable = false,
            length = 30
    )
    private AccountType accountType;

    @Column(
            name = "currency",
            nullable = false,
            length = 3
    )
    private String currency;

    @Column(
            name = "balance",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private AccountStatus status;

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

    protected Account() {
        // Required by JPA
    }

    public Account(
            String accountNumber,
            String cifId,
            AccountType accountType,
            String currency
    ) {
        this.accountNumber = accountNumber;
        this.cifId = cifId;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = AccountStatus.ACTIVE;
        }

        if (currency == null) {
            currency = "INR";
        }

        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCifId() {
        return cifId;
    }

    public void setCifId(String cifId) {
        this.cifId = cifId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
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