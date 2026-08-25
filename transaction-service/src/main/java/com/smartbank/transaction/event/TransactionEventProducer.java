package com.smartbank.transaction.event;

import com.smartbank.transaction.config.KafkaTopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventProducer {

//    private static final Logger log =
//            LoggerFactory.getLogger(
//                    TransactionEventProducer.class
//            );
//
//    private final KafkaTemplate<String,TransactionCreatedEvent> kafkaTemplate;
//
//    public TransactionEventProducer(KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void publishTransactionCreated(
//            TransactionCreatedEvent event
//    ) {
//        log.info(
//                "Publishing transaction.created event for transactionId={}",
//                event.transactionId()
//        );
//
//        kafkaTemplate.send(
//                KafkaTopicConfig.TRANSACTION_CREATED,
//                event.transactionId(),
//                event
//        );
//    }
}
