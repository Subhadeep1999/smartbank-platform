package com.smartbank.transaction.dto;

import com.smartbank.transaction.entity.TransactionStatus;
import com.smartbank.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,

        String transactionId,

        String accountNumber,

        TransactionType transactionType,

        BigDecimal amount,

        String currency,

        TransactionStatus status,

        String referenceNumber,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}
