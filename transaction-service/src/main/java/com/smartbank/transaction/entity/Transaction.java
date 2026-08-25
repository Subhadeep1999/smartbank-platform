package com.smartbank.transaction.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "transactions",
        schema = "transaction_schema",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_transaction_id",
                        columnNames = "transaction_id"
                ),
                @UniqueConstraint(
                        name = "uk_reference_number",
                        columnNames = "reference_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_transaction_id",
                        columnList = "transaction_id"
                ),
                @Index(
                        name = "idx_transaction_account_number",
                        columnList = "account_number"
                ),
                @Index(
                        name = "idx_transaction_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_transaction_created_at",
                        columnList = "created_at"
                )
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "transaction_id",
            nullable = false,
            unique = true,
            length = 30
    )
    private String transactionId;

    /*
     * Cross-service account identifier.
     *
     * Account belongs to Account Service.
     * Transaction Service stores accountNumber
     * as a business reference.
     *
     * No @ManyToOne / foreign key is used because
     * Account is owned by another microservice.
     */
    @Column(
            name = "account_number",
            nullable = false,
            length = 20
    )
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "transaction_type",
            nullable = false,
            length = 30
    )
    private TransactionType transactionType;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal amount;

    @Column(
            name = "currency",
            nullable = false,
            length = 3
    )
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private TransactionStatus status;

    @Column(
            name = "reference_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String referenceNumber;

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

    protected Transaction() {
        // Required by JPA
    }

    public Transaction(
            String transactionId,
            String accountNumber,
            TransactionType transactionType,
            BigDecimal amount,
            String currency,
            String referenceNumber
    ) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currency = currency;
        this.referenceNumber = referenceNumber;
        this.status = TransactionStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = TransactionStatus.PENDING;
        }

        if (currency == null) {
            currency = "INR";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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