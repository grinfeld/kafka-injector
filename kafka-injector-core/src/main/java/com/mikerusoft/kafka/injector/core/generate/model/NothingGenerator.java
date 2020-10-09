package com.mikerusoft.kafka.injector.core.generate.model;

import com.mikerusoft.kafka.injector.core.properties.Field;

public class NothingGenerator<T> implements DataGenerator<T> {
    @Override
    public T generate(Field[] fields) {
        return null;
    }
}
