package com.smartbank.transaction.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "processed_events",
        schema = "transaction_schema",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_processed_event_consumer",
                        columnNames = {
                                "event_id",
                                "consumer_name"
                        }
                )
        }
)
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "event_id",
            nullable = false,
            length = 100
    )
    private String eventId;

    @Column(
            name = "consumer_name",
            nullable = false,
            length = 100
    )
    private String consumerName;

    @Column(
            name = "processed_at",
            nullable = false
    )
    private LocalDateTime processedAt;

    protected ProcessedEvent() {
    }

    public ProcessedEvent(
            String eventId,
            String consumerName
    ) {
        this.eventId = eventId;
        this.consumerName = consumerName;
        this.processedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}