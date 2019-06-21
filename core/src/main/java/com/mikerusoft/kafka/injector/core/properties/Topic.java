package com.mikerusoft.kafka.injector.core.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    @JsonProperty("client_id_config")
    private String clientIdConfig;
    private String name;
    private Serializer serializer;
    private Generator[] generators;
    private Integer partitions=1;
    private Integer replicas=1;
}
