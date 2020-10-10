package com.mikerusoft.kafka.injector.core.generate.data;

/**
 * Always generates the same value
 * @param <T> what type it should be casted to.
 *           Since, not always it's possible explicit from value to decide what class to return.
 * @param <K> value to return
 */
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
