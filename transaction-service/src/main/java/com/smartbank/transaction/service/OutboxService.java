package com.smartbank.transaction.service;

import com.smartbank.transaction.entity.OutboxEvent;
import com.smartbank.transaction.entity.OutboxEventStatus;
import com.smartbank.transaction.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxService(
            OutboxEventRepository outboxEventRepository
    ) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public void replay(UUID eventId) {

        OutboxEvent event =
                outboxEventRepository.findById(eventId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Outbox event not found: "
                                                + eventId
                                )
                        );

        if (event.getStatus() != OutboxEventStatus.FAILED) {
            throw new IllegalStateException(
                    "Only FAILED outbox events can be replayed"
            );
        }

        event.setStatus(
                OutboxEventStatus.PENDING
        );

        event.resetRetryCount();

        event.setPublishedAt(null);

        outboxEventRepository.save(event);
    }
}