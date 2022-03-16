package org.paytm.milestone2._Milestone2.Kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    public String walletTopic = "walletTopic";
    public String transactionTopic = "transactionTopic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemp;

    public void publishToWalletTopic(String message) {
        System.out.println("Publishing to walletTopic "+walletTopic);
        this.kafkaTemp.send(walletTopic, message);
    }
    public void publishToTransactionTopic(String message) {
        System.out.println("Publishing to transactionTopic "+transactionTopic);
        this.kafkaTemp.send(transactionTopic, message);
    }
}
