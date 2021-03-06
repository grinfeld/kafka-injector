package com.mikerusoft.kafka.injector.core.streaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "builder")
public class TestNestedObject {
    private String str;
    private Integer number;
    private TestEnum testEnum;
}
