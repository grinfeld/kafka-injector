package com.mikerusoft.kafka.injector.core.generate.data;

import java.util.UUID;

public class UUIDGenerator implements ValueGenerator<String> {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Class<String> getCastTo() {
        return String.class;
    }
}
