package com.exmaple.sender.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;

@Slf4j
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.topics.reply}")
    private String REPLY_TOPICS;

    @Value("${spring.kafka.consumer.group-id}")
    private String CONSUMER_GROUPS;

    @Bean
    public ReplyingKafkaTemplate<String, Object, Object> replyingTemplate(ProducerFactory<String, Object> producerFactory, ConcurrentMessageListenerContainer<String, Object> repliesContainer) {
        ReplyingKafkaTemplate<String, Object, Object> replyTemplate = new ReplyingKafkaTemplate<>(producerFactory, repliesContainer);
        replyTemplate.setDefaultReplyTimeout(Duration.ofSeconds(10));
        replyTemplate.setSharedReplyTopic(true);
        return replyTemplate;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> repliesContainer(@Lazy KafkaTemplate<String, Object> kafkaTemplate, ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
        containerFactory.setReplyTemplate(kafkaTemplate);
        ConcurrentMessageListenerContainer<String, Object> repliesContainer = containerFactory.createContainer(REPLY_TOPICS);
        repliesContainer.getContainerProperties().setGroupId(CONSUMER_GROUPS);
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }
}
