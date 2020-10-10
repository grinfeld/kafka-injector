package com.mikerusoft.kafka.injector.core.generate.data;

import java.util.Random;

/**
 * Generates random number bounded by given start and end boundaries, received as string delimited by comma: "5,10"
 */
public abstract class PrimitiveNumberRangeGenerator extends RangeGenerator<Number> {

    public static PrimitiveNumberRangeGenerator factory(Class<?> type, String values) {
        if (!Number.class.isAssignableFrom(type))
            throw new IllegalArgumentException("Class doesn't extends Number. Class is " + type.getName());
        if (GeneratorUtils.isIntegerNumberType((Class<Number>)type))
            return new PrimitiveIntNumberRangeGenerator(type, values);
        if (GeneratorUtils.isFloatingNumberType((Class<Number>)type)) {
            return new FloatingPrimitiveNumberRangeGenerator(type, values);
        }
        throw new IllegalArgumentException("Class doesn't integer or floating types (please contact the administrator). Class is " + type.getName());
    }

    private Random random;
    private Class<?> type;

    public PrimitiveNumberRangeGenerator(Class<?> type, String values) {
        this.init(values, type);
    }

    private void init(String values, Class<?> type) {
        assertValidType(type);
        String[] splitted = values.split(",");
        @SuppressWarnings("unchecked")
        GeneratorUtils.Functions<Number> functions = GeneratorUtils.<Number>functions(type);
        init(functions.parse(splitted[0]), functions.parse(splitted[1]));
        this.random = new Random();
        this.type = type;
        assertRange();
    }

    protected void assertRange() {
        @SuppressWarnings("unchecked")
        GeneratorUtils.Functions<Number> functions = GeneratorUtils.<Number>functions(type);
        if (functions.compare(getStartFrom(), getEndTo()) > 0)
            throw new IllegalArgumentException("Range is invalid. Start " + getStartFrom() + ", " + getEndTo());
    }

    protected void assertValidType(Class<?> type) {
        GeneratorUtils.assertValidNumericType(type);
    }

    @Override
    public Number generate() {
        @SuppressWarnings("unchecked")
        GeneratorUtils.Functions<Number> functions = GeneratorUtils.<Number>functions(type);
        Number randomValue = functions.random(random);
        return normalize(randomValue);
    }

    public Class<?> getType() { return type; }
    public Random getRandom() { return random; }

    protected abstract Number normalize(Number value);

    @Override
    public Class<Number> getCastTo() {
        return (Class<Number>)type;
    }
}
