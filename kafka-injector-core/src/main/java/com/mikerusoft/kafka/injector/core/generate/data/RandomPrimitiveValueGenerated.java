package com.mikerusoft.kafka.injector.core.generate.data;

import java.util.Random;

public class RandomPrimitiveValueGenerated implements ValueGenerator<Number> {

    private Random random;
    private Class<?> type;

    public RandomPrimitiveValueGenerated(Class<?> type) {
        assertValidType(type);
        this.type = type;
        this.random = new Random();
    }

    protected void assertValidType(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Class couldn't be null");
        }
        if (!Number.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Class doesn't extends Number. Class is " + type.getName());
        }
    }

    @Override
    public Number generate() {
        GeneratorUtils.Functions<Number> functions = GeneratorUtils.<Number>functions(type);
        return functions.random(random);
    }

    @Override
    public Class<Number> getCastTo() {
        return (Class<Number>)type;
    }
}
