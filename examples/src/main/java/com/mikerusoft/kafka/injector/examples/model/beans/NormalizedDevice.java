package com.mikerusoft.kafka.injector.examples.model.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NormalizedDevice {
    private String value;
    private String type;
}
