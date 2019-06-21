package com.mikerusoft.kafka.injector.core.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Serializer {
    private String key;
    private String value;
}
