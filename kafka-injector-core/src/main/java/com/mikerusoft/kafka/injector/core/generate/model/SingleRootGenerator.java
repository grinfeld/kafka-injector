package com.mikerusoft.kafka.injector.core.generate.model;

import com.mikerusoft.kafka.injector.core.generate.data.FieldGeneratorFactory;
import com.mikerusoft.kafka.injector.core.properties.Field;

public class SingleRootGenerator implements DataGenerator<Object>  {

    @Override
    public Object generate(Field[] fields) {
        if (fields == null || fields.length != 1)
            throw new IllegalArgumentException("SingleRootGenerator should have only one field");
        Field field = fields[0];
        return FieldGeneratorFactory.generateFieldValue(field);
    }
}
