package com.brainstation23.data.consumerscaling.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaConsumerInfo {
    private String groupId;
    private String consumerId;
    private String listenerId;
    private Boolean active;
    private String activate;
    private String deactivate;
    private List<AssignmentInfo> assignments;
}
