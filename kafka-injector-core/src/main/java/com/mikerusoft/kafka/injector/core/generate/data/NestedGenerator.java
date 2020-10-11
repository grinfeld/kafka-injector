package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.properties.Field;
import com.mikerusoft.kafka.injector.core.properties.FieldContainerCreator;
import com.mikerusoft.kafka.injector.core.utils.Reflections;
import com.mikerusoft.kafka.injector.core.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Basic class for complicated/hierarchical Generators, such as List and Object.
 * @param <T>
 */
public abstract class NestedGenerator<T> implements ValueGenerator<T> {
    private Field[] fields;
    private Class<T> type;
    private FieldContainerCreator containerCreator;

    public NestedGenerator(Field[] fields, Class<T> type, FieldContainerCreator containerCreator) {
        this.containerCreator = containerCreator;
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

    @Override
    public T createContainerInstance() {
        try {
            if (this.containerCreator != null) {
                Class<?> clazzName = Class.forName(containerCreator.getClassName());
                String methodName = containerCreator.getMethodName();
                if (!Utils.isEmpty(methodName)) {
                    Method method = Reflections.findCreateMethod(clazzName, methodName);
                    if (Modifier.isStatic(method.getModifiers()) && containerCreator.isStatic()) {
                        return (T)method.invoke(null);
                    } else if (!Modifier.isStatic(method.getModifiers()) && !containerCreator.isStatic()) {
                        return (T)method.invoke(clazzName.newInstance());
                    } else {
                        throw new IllegalArgumentException("Invalid meta data for " + containerCreator);
                    }
                } else {
                    return (T)clazzName.newInstance();
                }
            }
            return type.newInstance();
        } catch (Exception e) {
            return Utils.rethrowRuntimeException(e);
        }
    }

    @Override
    public T generate() {
        T t = createContainerInstance();
        for (Field f : fields) {
            populateData(t, f);
        }
        return t;
    }

    private void populateData(T t, Field f) {
        try {
            Method method = populateMethod(f);
            Object generated = FieldGeneratorFactory.generateFieldValue(f);
            populate(generated, method, t, f);
        } catch (Exception e) {
            Utils.rethrowRuntimeException(e);
        }
    }

    @Override
    public Class<T> getCastTo() {
        return type;
    }
}
