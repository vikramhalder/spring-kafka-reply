package com.brainstation23.data.consumerscaling.controller;

import com.brainstation23.data.consumerscaling.config.KafkaConsumerConfig;
import com.brainstation23.data.consumerscaling.domain.AssignmentInfo;
import com.brainstation23.data.consumerscaling.domain.KafkaConsumerInfo;
import com.brainstation23.data.consumerscaling.domain.KafkaTopicInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
@SuppressWarnings("all")
@RequestMapping(path = "/api/kafka")
public class KafkaInfoController {

    final KafkaConsumerConfig kafkaConsumerConfig;
    final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @GetMapping("/registry")
    public List<KafkaConsumerInfo> getConsumerIds() {
        final Stream<KafkaConsumerInfo> stream = kafkaListenerEndpointRegistry.getListenerContainers()
                .stream()
                .map(listenerContainer -> {
                    String consumerId = listenerContainer.getListenerId();
                    return KafkaConsumerInfo.builder()
                            .consumerId(consumerId)
                            .groupId(listenerContainer.getGroupId())
                            .listenerId(listenerContainer.getListenerId())
                            .active(listenerContainer.isRunning())
                            .activate(getClass().getAnnotation(RequestMapping.class).path()[0].concat("/activate/").concat(consumerId))
                            .deactivate(getClass().getAnnotation(RequestMapping.class).path()[0].concat("/deactivate/").concat(consumerId))
                            .assignments(Optional.ofNullable(listenerContainer.getAssignedPartitions())
                                    .map(topicPartitions -> topicPartitions.stream()
                                            .map(topicPartition -> {
                                                return AssignmentInfo.builder().topic(topicPartition.topic()).partition(topicPartition.partition()).build();
                                            })
                                            .collect(Collectors.toList()))
                                    .orElse(null)
                            ).build();
                });
        return stream.toList();
    }

    @GetMapping(path = "/deactivate/{consumerId}")
    public Map<String, Object> deactivateConsumer(@PathVariable String consumerId) {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            return new HashMap<>() {{
                put("error", true);
                put("result", null);
                put("message", String.format("Consumer with id %s is not found", consumerId));
            }};
        } else if (!listenerContainer.isRunning()) {
            return new HashMap<>() {{
                put("error", true);
                put("result", null);
                put("message", String.format("Consumer with id %s is already stop", consumerId));
            }};
        } else {
            listenerContainer.stop();
            return new HashMap<>() {{
                put("error", false);
                put("message", null);
                put("result", String.format("Consumer with id %s is already stop", consumerId));
            }};
        }
    }

    @GetMapping(path = "/activate/{consumerId}")
    public Map<String, Object> activateConsumer(@PathVariable String consumerId) {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            return new HashMap<>() {{
                put("error", true);
                put("result", null);
                put("message", String.format("Consumer with id %s is not found", consumerId));
            }};
        } else if (listenerContainer.isRunning()) {
            return new HashMap<>() {{
                put("error", true);
                put("result", null);
                put("message", String.format("Consumer with id %s is already running", consumerId));
            }};
        } else {
            listenerContainer.start();
            return new HashMap<>() {{
                put("error", false);
                put("message", null);
                put("result", String.format("Running a consumer with id %s", consumerId));
            }};
        }
    }

    @GetMapping("/create-consumer/{topic}/{groupId}")
    public Map<String, Object> startConsumer(@PathVariable String topic, @PathVariable String groupId) throws Exception {
        final KafkaTopicInfo kafkaTopicInfo = KafkaTopicInfo.builder().topic(topic).groupId(groupId).build();
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(kafkaTopicInfo.getId());
        if (Objects.isNull(listenerContainer)) {
            kafkaConsumerConfig.createContainer(kafkaTopicInfo);
            return new HashMap<>() {{
                put("error", false);
                put("message", null);
                put("result", String.format("Consumer started for topic: %s with groupId: %s", topic, groupId));
            }};
        } else if (listenerContainer.isRunning()) {
            return new HashMap<>() {{
                put("error", true);
                put("result", null);
                put("message", String.format("Consumer with id %s is already running", kafkaTopicInfo.getId()));
            }};
        } else {
            listenerContainer.start();
            return new HashMap<>() {{
                put("error", false);
                put("message", null);
                put("result", String.format("Running a consumer with id %s", kafkaTopicInfo.getId()));
            }};
        }
    }
}
