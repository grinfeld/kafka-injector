package com.mikerusoft.kafka.injector.core.generate.data;

public class NullValueGenerator implements ValueGenerator<Object> {
    @Override
    public Object generate() {
        return null;
    }

    @Override
    public Class<Object> getCastTo() {
        return Object.class;
    }
}
