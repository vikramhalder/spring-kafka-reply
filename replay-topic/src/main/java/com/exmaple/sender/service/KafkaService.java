package com.exmaple.sender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {
    @Value("${spring.kafka.topics.send}")
    private String SEND_TOPICS;

    final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    public Object kafkaRequestReply(Object request) throws Exception {
        final ProducerRecord<String, Object> record = new ProducerRecord<>(SEND_TOPICS, request);
        final RequestReplyFuture<String, Object, Object> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
        final ConsumerRecord<String, Object> consumerRecord = replyFuture.get(1, TimeUnit.MINUTES);
        assert consumerRecord != null;
        return consumerRecord.value();
    }
}
