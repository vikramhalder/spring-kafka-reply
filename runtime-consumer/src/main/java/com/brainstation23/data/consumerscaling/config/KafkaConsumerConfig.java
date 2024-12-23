package com.brainstation23.data.consumerscaling.config;

import com.brainstation23.data.consumerscaling.consumer.KafkaDynamicListener;
import com.brainstation23.data.consumerscaling.domain.KafkaTopicInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

@Slf4j
@Service
@SuppressWarnings("all")
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    final KafkaDynamicListener kafkaDynamicListener;
    final ConsumerFactory<String, String> consumerFactory;
    final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    public void createContainer(KafkaTopicInfo topicInfo) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);

        final MethodKafkaListenerEndpoint<String, String> methodKafkaListenerEndpoint = new MethodKafkaListenerEndpoint<>();
        methodKafkaListenerEndpoint.setId(topicInfo.getId());
        methodKafkaListenerEndpoint.setBean(kafkaDynamicListener);
        methodKafkaListenerEndpoint.setGroupId(topicInfo.getGroupId());
        methodKafkaListenerEndpoint.setTopics(topicInfo.getTopic());
        methodKafkaListenerEndpoint.setMethod(Objects.requireNonNull(ReflectionUtils.findMethod(kafkaDynamicListener.getClass(), "onMessage", ConsumerRecord.class)));
        methodKafkaListenerEndpoint.setMessageHandlerMethodFactory(InvocableHandlerMethod::new);
        kafkaListenerEndpointRegistry.registerListenerContainer(methodKafkaListenerEndpoint, factory);
        kafkaListenerEndpointRegistry.getListenerContainer(topicInfo.getId()).start();
    }
}