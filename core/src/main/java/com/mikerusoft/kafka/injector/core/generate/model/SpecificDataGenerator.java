package com.mikerusoft.kafka.injector.core.generate.model;

import com.mikerusoft.kafka.injector.core.generate.data.FieldGeneratorFactory;
import com.mikerusoft.kafka.injector.core.properties.Field;
import com.mikerusoft.kafka.injector.core.utils.Reflections;
import com.mikerusoft.kafka.injector.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public abstract class SpecificDataGenerator<T, B> implements DataGenerator<T> {
    protected abstract B createBuilder();
    protected abstract T generateObject(B builder);

    @Override
    public T generate(Field[] fields) {
        B builder = createBuilder();
        for (Field f : fields) {
            try {
                Method method = Reflections.findConsumerMethod(builder.getClass(), f.getName(), f.getCast());
                Object generated = FieldGeneratorFactory.generateFieldValue(f);
                method.invoke(builder, generated);
            } catch (Exception e) {
                Utils.rethrowRuntimeException(e);
            }
        }
        return generateObject(builder);
    }
}
