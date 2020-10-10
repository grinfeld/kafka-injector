package com.mikerusoft.kafka.injector.core.properties;

import java.util.Optional;

public enum GeneratorType {
    REGEX, NIL, RANDOM, EMPTY, FIXED, RANGE, SEQUENTIAL_RANGE, LIST, RANDOM_LIST, TIMESTAMP, TIMESTAMP_REGEX,
    NESTED_LIST, NESTED_OBJECT, ENUM, UUID;
    private static final String EMPTY_STRING = "";
    public static GeneratorType byString(String value) {
        try {
            return GeneratorType.valueOf(
                Optional.ofNullable(value).orElse(EMPTY_STRING).toUpperCase()
            );
        } catch (Exception e) {
            throw new RuntimeException("Unable to find generator type " + value);
        }
    }
}
