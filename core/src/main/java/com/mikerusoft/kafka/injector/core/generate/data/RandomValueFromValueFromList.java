package com.mikerusoft.kafka.injector.core.generate.data;

import java.util.List;
import java.util.Random;

public class RandomValueFromValueFromList<T> extends ValueFromListGenerator<T> {

    private Random random;

    public RandomValueFromValueFromList(Class<T> type, String values) {
        super(type, values);
        if (values == null)
            throw new IllegalArgumentException("Values couldn't be null for RandomValueFromValueFromList");
        this.random = new Random();
    }

    public RandomValueFromValueFromList(Class<T> type, List<T> t) {
        super(type, t);
        this.random = new Random();
    }

    @Override
    protected int calculate() {
        return random.nextInt(this.getSize());
    }
}
