package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequentialRangeGenerator<T extends Number> implements ValueGenerator<T> {

    private T size;
    private T next;
    private T start;

    private Class<T> type;

    public SequentialRangeGenerator(String values, Class<?> type) {
        GeneratorUtils.assertValidIntType(type);

        this.type = (Class<T>)type;
        if ("".equals(Utils.deNull(values, ""))) {
            throw new IllegalArgumentException("value shouldn't be empty");
        }

        String[] split = values.split(",");
        if (split.length <= 1) {
            throw new IllegalArgumentException("Value should contain at least 2 values");
        }
        init(split);
    }

    private void init(String[] split) {
        start = (T) GeneratorUtils.functions(type).parse(split[0]);
        T end = (T) GeneratorUtils.functions(type).parse(split[1]);
        size = (T) GeneratorUtils.functions(type).subtract(end, start);
        next = (T) GeneratorUtils.functions(type).parse(String.valueOf(-1));
    }

    @Override
    public T generate() {
        // I don't want to deal with synchronization. Maximum in case ofDuration race condition method will return same value for 2 or more threads
        T next = (T) GeneratorUtils.functions(type).getAdd().apply(this.next, new Long(1));
        this.next = (T) GeneratorUtils.functions(type).getMod().apply(next, size);
        T apply = (T) GeneratorUtils.functions(type).getAdd().apply(next, start);
        return apply;
    }

    @Override
    public Class<T> getCastTo() {
        return type;
    }

}
