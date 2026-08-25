package com.smartbank.transaction.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbank.transaction.entity.ProcessedEvent;
import com.smartbank.transaction.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionAuditConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(
                    TransactionAuditConsumer.class
            );

    private static final String CONSUMER_NAME =
            "transaction-audit-consumer";

    private final ObjectMapper objectMapper;

    private final ProcessedEventRepository
            processedEventRepository;

    public TransactionAuditConsumer(
            ObjectMapper objectMapper,
            ProcessedEventRepository processedEventRepository
    ) {
        this.objectMapper = objectMapper;
        this.processedEventRepository =
                processedEventRepository;
    }

    @Transactional
    @KafkaListener(
            topics = "transaction.created",
            groupId = CONSUMER_NAME
    )
    public void consumerTransactionCreated(
            String payload
    ) {

        try {

            TransactionCreatedEvent event =
                    objectMapper.readValue(
                            payload,
                            TransactionCreatedEvent.class
                    );

            String eventId = event.eventId();

            boolean alreadyProcessed =
                    processedEventRepository
                            .existsByEventIdAndConsumerName(
                                    eventId,
                                    CONSUMER_NAME
                            );

            if (alreadyProcessed) {

                log.warn(
                        "Duplicate transaction.created event ignored | " +
                                "eventId={} | transactionId={}",
                        eventId,
                        event.transactionId()
                );

                return;
            }

            log.info(
                    "Received transaction.created event | " +
                            "eventId={} | " +
                            "transactionId={} | " +
                            "accountNumber={} | " +
                            "type={} | " +
                            "amount={} | " +
                            "currency={} | " +
                            "status={}",
                    event.eventId(),
                    event.transactionId(),
                    event.accountNumber(),
                    event.transactionType(),
                    event.amount(),
                    event.currency(),
                    event.status()
            );

            processedEventRepository.save(
                    new ProcessedEvent(
                            eventId,
                            CONSUMER_NAME
                    )
            );

            log.info(
                    "Transaction event marked as processed | " +
                            "eventId={}",
                    eventId
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to process transaction.created event | " +
                            "payload={}",
                    payload,
                    ex
            );

            throw new IllegalStateException(
                    "Failed to process transaction.created event",
                    ex
            );
        }
    }
}