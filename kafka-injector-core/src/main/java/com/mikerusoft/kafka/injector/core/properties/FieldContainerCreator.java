package com.mikerusoft.kafka.injector.core.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class FieldContainerCreator {
    @JsonProperty("class-name")
    private String className;
    @JsonProperty("method-name")
    private String methodName;
    @JsonProperty("static")
    private boolean aStatic;

    @JsonIgnore
    public boolean isStatic() {
        return aStatic;
    }
}
