package com.smartbank.transaction.event;

import com.smartbank.transaction.entity.TransactionStatus;
import com.smartbank.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCreatedEvent(

        String eventId,

        String transactionId,

        String accountNumber,

        TransactionType transactionType,

        BigDecimal amount,

        String currency,

        TransactionStatus status,

        String referenceNumber,

        LocalDateTime createdAt
) {
}