package com.exmaple.sender.consumer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public class KafkaReplay {

    @SendTo
    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topics.send}")
    public Message<?> listen(ConsumerRecord<String, Object> consumerRecord) {
        String reversedString = "Replay: " + new Date();
        return MessageBuilder.withPayload(reversedString).build();
    }
}
