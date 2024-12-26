package com.brainstation23.data.consumerscaling.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentInfo {
    private String topic;
    private Integer partition;
}