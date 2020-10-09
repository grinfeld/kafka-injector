package com.mikerusoft.kafka.injector.core.streaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestObject {
    private TestNestedObject nested;
}
