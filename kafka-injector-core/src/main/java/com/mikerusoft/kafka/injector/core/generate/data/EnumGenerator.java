package com.mikerusoft.kafka.injector.core.generate.data;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class EnumGenerator<T> extends RandomValueFromValueFromList<T> {

    private T value;

    public EnumGenerator(Class<T> type, String value) {
        super(type, createEnums(type));
        this.value = (T)Enum.valueOf((Class<? extends Enum>)type, value);
    }

    private static <T> List<T> createEnums(Class<T> type) {
        if (!type.isEnum()) {
            throw new IllegalArgumentException("Expected Enum and not " + type.toString());
        }
        @SuppressWarnings("unchecked")
        EnumSet enumSet = EnumSet.allOf((Class<? extends Enum>) type);
        return new ArrayList<>((Set<T>)enumSet);
    }

    @Override
    public T generate() {
        return value == null ? super.generate() : value;
    }

    @Override
    public Class<T> getCastTo() {
        return super.getCastTo();
    }
}
