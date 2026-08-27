package com.smartbank.notification.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);


    @KafkaListener(
            topics = "transaction.created",
            groupId = "notification-consumer"
    )
    public void consume(
            String payload
    ) {
        log.info(
                "Notification Service received transaction.created event: {}",
                payload
        );

        if (payload.contains("FAIL-NOTIFICATION")) {

            log.error(
                    "Simulated notification processing failure"
            );

            throw new RuntimeException(
                    "Simulated notification failure"
            );
        }

        log.info(
                "Notification processed successfully"
        );
    }
}
