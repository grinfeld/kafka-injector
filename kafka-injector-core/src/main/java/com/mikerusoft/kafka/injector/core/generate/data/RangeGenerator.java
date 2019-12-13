package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class RangeGenerator<T extends Number> implements ValueGenerator<T> {
    private Pair<T, T> values;

    protected void init(T start, T end) {
        this.values = Pair.of(start, end);
    }

    public T getStartFrom() { return this.getValues().getLeft(); }
    public T getEndTo() { return this.getValues().getRight(); }
}
