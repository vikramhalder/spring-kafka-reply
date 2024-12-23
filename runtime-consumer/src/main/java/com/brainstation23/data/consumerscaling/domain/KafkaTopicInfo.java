package com.brainstation23.data.consumerscaling.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class KafkaTopicInfo {
    private String topic;

    private String groupId;

    public String getId(){
        return getGroupId().concat("-").concat(getTopic()).toLowerCase();
    }
}
