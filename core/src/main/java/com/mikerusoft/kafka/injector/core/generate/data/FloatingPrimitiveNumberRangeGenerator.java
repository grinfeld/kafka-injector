package com.mikerusoft.kafka.injector.core.generate.data;

public class FloatingPrimitiveNumberRangeGenerator extends PrimitiveNumberRangeGenerator {

    public FloatingPrimitiveNumberRangeGenerator(Class<?> type, String values) {
        super(type, values);
    }

    @Override
    protected void assertValidType(Class<?> type) {
        GeneratorUtils.assertValidFloatingType(type);
    }

    @Override
    protected Number normalize(Number value) {
        int integerValue = getRandom().nextInt(getEndTo().intValue() - getStartFrom().intValue());
        if (integerValue < getStartFrom().intValue()) {
            integerValue = integerValue + getStartFrom().intValue();
        }
        @SuppressWarnings("unchecked")
        GeneratorUtils.Functions<Number> functions = GeneratorUtils.<Number>functions(getType());
        return functions.add(value, integerValue);
    }
}
