package com.mikerusoft.kafka.injector.core.generate.data;

/**
 * @see PrimitiveNumberRangeGenerator
 */
public class PrimitiveIntNumberRangeGenerator extends PrimitiveNumberRangeGenerator {

    public PrimitiveIntNumberRangeGenerator(Class<?> type, String values) {
        super(type, values);
    }

    @Override
    protected void assertValidType(Class<?> type) {
        GeneratorUtils.assertValidIntType(type);
    }

    @Override
    protected Number normalize(Number value) {
        Number min = this.getStartFrom();
        Number end = this.getEndTo();

        @SuppressWarnings("unchecked")
        GeneratorUtils.Functions<Number> functions = GeneratorUtils.<Number>functions(getType());
        if (functions.compare(value, end) > 0) {
            // let's limit value to be maximum as end value
            value = functions.mod(value, end);
        }
        if (functions.compare(value, min) < 0) {
            Number pivot = functions.subtract(end, min);
            if (functions.compare(value, pivot) < 0) {
                // if value is less then pivot, let's add minimum (so we won't be greater then maximum for sure
                value = functions.add(value, min);
            } else {
                // if value is more then pivot, we add pivot since, else value will be greater then end
                value = functions.add(value, pivot);
            }
        }
        return value;
    }
}
