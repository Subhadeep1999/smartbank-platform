package com.smartbank.transaction.controller;

import com.smartbank.transaction.dto.CreateTransactionRequest;
import com.smartbank.transaction.dto.TransactionResponse;
import com.smartbank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(request));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String transactionId
    ) {

        return ResponseEntity.ok(
                transactionService.getTransaction(transactionId)
        );
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>>
    getTransactionsByAccount(
            @PathVariable String accountNumber
    ) {

        return ResponseEntity.ok(
                transactionService
                        .getTransactionsByAccount(accountNumber)
        );
    }
}