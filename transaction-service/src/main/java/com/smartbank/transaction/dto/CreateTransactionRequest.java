package com.smartbank.transaction.dto;

import com.smartbank.transaction.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateTransactionRequest(

        @NotBlank(message = "Account number is required")
        String accountNumber,

        @NotNull(message = "Transaction type is required")
        TransactionType transactionType,

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Amount must be greater than zero"
        )
        BigDecimal amount,

        @Pattern(
                regexp = "^[A-Z]{3}$",
                message = "Currency must be a valid 3-letter code"
        )
        String currency,

        @NotBlank(message = "Reference number is required")
        String referenceNumber
) {
}