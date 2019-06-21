package com.mikerusoft.kafka.injector.core.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikerusoft.kafka.injector.core.generate.model.DataGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Generator {
    @JsonIgnore private DataGenerator<?> generator;
    @JsonIgnore private String uid = java.util.UUID.randomUUID().toString();
    private Field[] fields;
    private long delayAfter;
    private long interval;
    private long instances=1;
    private long takeWhile;


    @JsonProperty("type")
    public String getType() {
        return generator == null ? null : generator.getClass().getName();
    }

    @JsonProperty("type")
    public void setType(String type) {
        try {
            this.generator = (DataGenerator)Class.forName(type).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Filed to create DataGenerator with class" + type, e);
        }
    }
}
