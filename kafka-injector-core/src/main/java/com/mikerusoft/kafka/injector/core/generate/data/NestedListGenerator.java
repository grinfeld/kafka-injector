package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.properties.Field;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class NestedListGenerator<V> extends NestedGenerator<ArrayList> {

    private Class<V> innerClass;

    public NestedListGenerator(Field[] fields, Class<V> type) {
        super(fields, ArrayList.class, null);
        innerClass = type;
    }

    @Override
    protected Method populateMethod(Field field) throws ReflectiveOperationException {
        return ArrayList.class.getMethod("add", Object.class);
    }

    @Override
    protected void populate(Object generated, Method method, ArrayList containedObject, Field field) throws ReflectiveOperationException {
        super.populate(generated, method, containedObject, field);
    }
}
