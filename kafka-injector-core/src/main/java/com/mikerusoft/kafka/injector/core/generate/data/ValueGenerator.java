package com.mikerusoft.kafka.injector.core.generate.data;

public interface ValueGenerator<T> {
    T generate();
    Class<T> getCastTo();
    default T createContainerInstance() {
        return null;
    }
}
