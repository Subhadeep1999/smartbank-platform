package com.smartbank.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbank.transaction.client.AccountServiceClient;
import com.smartbank.transaction.config.KafkaTopicConfig;
import com.smartbank.transaction.dto.CreateTransactionRequest;
import com.smartbank.transaction.dto.TransactionResponse;
import com.smartbank.transaction.entity.OutboxEvent;
import com.smartbank.transaction.entity.Transaction;
import com.smartbank.transaction.event.TransactionCreatedEvent;
import com.smartbank.transaction.event.TransactionEventProducer;
import com.smartbank.transaction.exception.DuplicateTransactionException;
import com.smartbank.transaction.exception.TransactionNotFoundException;
import com.smartbank.transaction.repository.OutboxEventRepository;
import com.smartbank.transaction.repository.TransactionRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountServiceClient accountServiceClient, TransactionEventProducer transactionEventProducer, OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }


    @Override
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {

        if (!accountServiceClient.accountExists(
                request.accountNumber()
        )) {
            throw new ResourceNotFoundException(
                    "Account not found: "
                            + request.accountNumber()
            );
        }

        if(transactionRepository.existsByReferenceNumber(
                request.referenceNumber())
        ) {
            throw new DuplicateTransactionException(
                    "Transaction reference already exists: "
                            + request.referenceNumber()
            );
        }

        String transactionId =
                "TXN" + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 20)
                        .toUpperCase();

        Transaction transaction = new Transaction(
                transactionId,
                request.accountNumber(),
                request.transactionType(),
                request.amount(),
                request.currency(),
                request.referenceNumber()
        );

        Transaction saved =
                transactionRepository.save(transaction);

        // Create outbox event first.
        // ID is generated immediately by the constructor.
        OutboxEvent outboxEvent =
                new OutboxEvent(
                        "TRANSACTION",
                        saved.getTransactionId(),
                        "TransactionCreated",
                        KafkaTopicConfig.TRANSACTION_CREATED,
                        null
                );

        // Now we already have the event ID.
        String eventId =
                outboxEvent.getId().toString();

        TransactionCreatedEvent event =
                new TransactionCreatedEvent(
                        eventId,
                        saved.getTransactionId(),
                        saved.getAccountNumber(),
                        saved.getTransactionType(),
                        saved.getAmount(),
                        saved.getCurrency(),
                        saved.getStatus(),
                        saved.getReferenceNumber(),
                        saved.getCreatedAt()
                );

//        transactionEventProducer.publishTransactionCreated(event); ==> Publish is being done through outbox

        String payload;

        try{
            payload = objectMapper.writeValueAsString(event);
        }catch(JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Failed to serialize transaction event",
                    ex
            );
        }

        outboxEvent.setPayload(payload);


        outboxEventRepository.save(outboxEvent);

        return mapToResponse(saved);
    }

    @Override
    public TransactionResponse getTransaction(String transactionId) {

        Transaction transaction =
                transactionRepository
                        .findByTransactionId(transactionId)
                        .orElseThrow(() ->
                                new TransactionNotFoundException(
                                        "Transaction not found: "
                                                + transactionId
                                )
                        );

        return mapToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getTransactionsByAccount(String accountNumber) {

        return transactionRepository
                .findByAccountNumber(accountNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    private TransactionResponse mapToResponse(
            Transaction transaction
    ) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionId(),
                transaction.getAccountNumber(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getStatus(),
                transaction.getReferenceNumber(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
