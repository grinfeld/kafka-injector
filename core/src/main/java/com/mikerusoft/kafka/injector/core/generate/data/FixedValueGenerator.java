package com.mikerusoft.kafka.injector.core.generate.data;

public class FixedValueGenerator<T, K> implements ValueGenerator<T> {
    private K value;
    private Class<T> castTo;

    public FixedValueGenerator(K value, Class<T> castTo) {
        this.value = value;
        this.castTo = castTo;
    }

    @Override
    public T generate() {
        if (value == null)
            return (T)value;
        return String.class.equals(value.getClass()) ? (T)GeneratorUtils.functions(castTo).parse((String)value) : (T)value;
    }

    @Override
    public Class<T> getCastTo() {
        return castTo;
    }

}
