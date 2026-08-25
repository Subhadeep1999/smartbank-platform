package com.smartbank.transaction.service;

import com.smartbank.transaction.dto.CreateTransactionRequest;
import com.smartbank.transaction.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(
            CreateTransactionRequest request
    );

    TransactionResponse getTransaction(
            String transactionId
    );

    List<TransactionResponse> getTransactionsByAccount(
            String accountNumber
    );
}