package com.mikerusoft.kafka.injector.core.generate.data;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Class to generate Enums
 * @param <T> enum
 */
public class EnumGenerator<T> extends RandomValueFromValueFromList<T> {

    private T value;

    /**
     *
     * @param type class of enum
     * @param value if not null, this value will be returned for every {@link EnumGenerator#generate()}, else random value from Enum.values() will be returned every time
     * @throws IllegalArgumentException if type is not enum constant
     */
    public EnumGenerator(Class<T> type, String value) {
        super(type, createEnums(type));
        this.value = value == null ? null : (T)Enum.valueOf((Class<? extends Enum>)type, value);
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
