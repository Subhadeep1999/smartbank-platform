package com.smartbank.transaction.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String TRANSACTION_CREATED =
            "transaction.created";

    @Bean
    public NewTopic transactionCreatedTopic() {
        return new NewTopic(
                TRANSACTION_CREATED,
                3,
                (short) 1
        );
    }
}
