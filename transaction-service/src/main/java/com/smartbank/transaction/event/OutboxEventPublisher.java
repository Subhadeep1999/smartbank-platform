package com.smartbank.transaction.event;

import com.smartbank.transaction.entity.OutboxEvent;
import com.smartbank.transaction.entity.OutboxEventStatus;
import com.smartbank.transaction.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxEventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final int maxRetries;

    public OutboxEventPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${outbox.publisher.max-retries:3}")
            int maxRetries
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.maxRetries = maxRetries;
    }

    @Scheduled(
            fixedDelayString =
                    "${outbox.publisher.fixed-delay-ms:5000}"
    )
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findTop100ByStatusOrderByCreatedAtAsc(
                                OutboxEventStatus.PENDING
                        );

        for (OutboxEvent event : events) {
            publishEvent(event);
        }
    }

    @Transactional
    protected void publishEvent(OutboxEvent event) {

        try {

            kafkaTemplate
                    .send(
                            event.getTopic(),
                            event.getAggregateId(),
                            event.getPayload()
                    )
                    .get();

            event.setStatus(
                    OutboxEventStatus.PUBLISHED
            );

            event.setPublishedAt(
                    LocalDateTime.now()
            );

            outboxEventRepository.save(event);

            log.info(
                    "Published outbox event | eventId={} | " +
                            "aggregateId={} | topic={} | retries={}",
                    event.getId(),
                    event.getAggregateId(),
                    event.getTopic(),
                    event.getRetryCount()
            );

        } catch (Exception ex) {

            event.incrementRetryCount();

            if (event.getRetryCount() >= maxRetries) {

                event.setStatus(
                        OutboxEventStatus.FAILED
                );

                log.error(
                        "Outbox event permanently failed | " +
                                "eventId={} | aggregateId={} | " +
                                "retryCount={}",
                        event.getId(),
                        event.getAggregateId(),
                        event.getRetryCount(),
                        ex
                );

            } else {

                log.warn(
                        "Outbox event publish failed. " +
                                "Will retry | eventId={} | " +
                                "aggregateId={} | retryCount={}/{}",
                        event.getId(),
                        event.getAggregateId(),
                        event.getRetryCount(),
                        maxRetries,
                        ex
                );
            }

            outboxEventRepository.save(event);
        }
    }
}