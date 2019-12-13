package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates values from list in serial order
 * @param <T>
 */
public class ValueFromListGenerator<T> implements ValueGenerator<T> {

    private T[] values;
    private int counter;
    private int size;
    private Class<T> type;

    public ValueFromListGenerator(Class<T> type, String values) {
        this.type = type;
        init(type,
            (List<T>)Stream.of(Utils.deNull(values, "").split(","))
                .map(t -> GeneratorUtils.functions(type).parse(t)).collect(Collectors.toList())
        );
    }

    public ValueFromListGenerator(Class<T> type, List<T> t) {
        init(type, t);
    }

    public void init(Class<T> type, List<T> t) {
        List<T> data = Optional.ofNullable(t).orElseGet(Collections::emptyList);
        this.values = Utils.createArray(type, data.size());
        this.values = data.toArray(values);
        this.counter = -1;
        this.size = values.length;
    }

    @Override
    public T generate() {
        // I don't want to deal with synchronization. Maximum in case ofDuration race condition method will return same value for 2 or more threads
        counter = calculate();
        return values[counter];
    }

    @Override
    public Class<T> getCastTo() {
        return this.type;
    }

    public int getSize() { return size; }

    protected int calculate() {
        return (counter + 1) % getSize();
    }
}
