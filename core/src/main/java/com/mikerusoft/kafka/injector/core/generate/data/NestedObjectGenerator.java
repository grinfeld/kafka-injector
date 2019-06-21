package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.properties.Field;

public class NestedObjectGenerator<T> extends NestedGenerator<T> {
    public NestedObjectGenerator(Field[] fields, Class<T> type) {
        super(fields, type);
    }
}
