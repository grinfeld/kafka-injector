package com.mikerusoft.kafka.injector.core.generate.model;

import com.mikerusoft.kafka.injector.core.properties.Field;

public interface DataGenerator<T> {
    T generate(Field[] fields);
}
