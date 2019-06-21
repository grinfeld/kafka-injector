package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.properties.Field;
import com.mikerusoft.kafka.injector.core.utils.Reflections;
import com.mikerusoft.kafka.injector.core.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public abstract class NestedGenerator<T> implements ValueGenerator<T> {
    private Field[] fields;
    private Class<T> type;

    public NestedGenerator(Field[] fields, Class<T> type) {
        this.type = type;
        if (fields == null || fields.length == 0)
            throw new IllegalArgumentException("For nested object fields shouldn't be null or empty");
        this.fields = fields;
    }

    protected Method populateMethod(Field field) throws ReflectiveOperationException {
        return Reflections.findConsumerMethod(type, field.getName(), field.getCast());
    }

    protected void populate(Object generated, Method method, T containedObject, Field field) throws ReflectiveOperationException {
        method.invoke(containedObject, generated);
    }

    protected T createInstance() throws ReflectiveOperationException {
        Constructor<T> constructor = type.getConstructor();
        return constructor.newInstance();
    }

    @Override
    public T generate() {
        try {
            T t = createInstance();
            for (Field f : fields) {
                try {
                    Method method = populateMethod(f);
                    Object generated = FieldGeneratorFactory.generateFieldValue(f);
                    populate(generated, method, t, f);
                } catch (Exception e) {
                    Utils.rethrowRuntimeException(e);
                }
            }
            return t;
        } catch (Exception e) {
            Utils.rethrowRuntimeException(e);
        }
        return null;
    }

    @Override
    public Class<T> getCastTo() {
        return type;
    }
}
