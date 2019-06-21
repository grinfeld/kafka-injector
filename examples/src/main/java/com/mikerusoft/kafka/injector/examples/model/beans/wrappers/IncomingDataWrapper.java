package com.mikerusoft.kafka.injector.examples.model.beans.wrappers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "typ", defaultImpl = StamMessageWrapper.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class IncomingDataWrapper<T> {
    private String networkType;
    private String sourceType;
    private T body;
    protected boolean kafkafied;
}
