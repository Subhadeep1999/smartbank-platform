package com.smartbank.transaction.repository;

import com.smartbank.transaction.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, UUID> {

    boolean existsByEventIdAndConsumerName(
            String eventId,
            String consumerName
    );
}