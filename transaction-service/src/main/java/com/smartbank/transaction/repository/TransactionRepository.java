package com.smartbank.transaction.repository;

import com.smartbank.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findByAccountNumber(String accountNumber);

    boolean existsByReferenceNumber(String referenceNumber);
}