package com.smartbank.transaction.repository;

import com.smartbank.transaction.entity.OutboxEvent;
import com.smartbank.transaction.entity.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status
    );

    Optional<OutboxEvent> findById(UUID id);
}