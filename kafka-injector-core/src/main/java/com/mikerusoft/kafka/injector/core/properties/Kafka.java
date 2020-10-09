package com.mikerusoft.kafka.injector.core.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Kafka {
    private String url;
    private boolean createTopics;
    private Integer batchSize;
    private int lingerMs = 1;
    private Topic[] topics;
}
