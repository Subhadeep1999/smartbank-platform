package com.smartbank.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) ->
                        new TopicPartition(
                                record.topic() + ".DLT",
                                record.partition()
                        )
        );
    }

    @Bean
    public DefaultErrorHandler errorHandler(
            DeadLetterPublishingRecoverer recoverer
    ) {

        FixedBackOff backOff =
                new FixedBackOff(
                        2000L,
                        2L
                );
        return new DefaultErrorHandler(
                recoverer,
                backOff
        );
    }


    public NewTopic transactionCreatedAt() {

        return new NewTopic(
                "transaction.created.DLT",
                3,
                (short) 1
        );
    }
}
