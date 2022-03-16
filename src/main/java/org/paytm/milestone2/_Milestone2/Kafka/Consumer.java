package org.paytm.milestone2._Milestone2.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

    @KafkaListener(topics="walletTopic", groupId="walletManagementTopics")
    public void consumeFromWalletTopic(String message) {
        System.out.println("Consummed message "+message);
    }

    @KafkaListener(topics="transactionTopic", groupId="walletManagementTopics")
    public void consumeFromTransactionTopic(String message) {
        System.out.println("Consummed message"+message);
    }

}