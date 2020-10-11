package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.properties.Field;
import com.mikerusoft.kafka.injector.core.utils.Utils;

import java.util.Map;

/**
 * Class that generates Map and values according to defined names and fields
 */
public class MapGenerator implements ValueGenerator<Map> {

    private Field[] fields;
    private Class<Map> type;

    public MapGenerator(Field[] fields, Class<?> type) {
        if (!Map.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Class should be child of java,util.Map");
        }
        this.type = (Class<Map>)type;
        if (fields == null || fields.length == 0)
            throw new IllegalArgumentException("For nested object fields shouldn't be null or empty");
        this.fields = fields;
    }

    @Override
    public Map generate() {
        Map map = createContainerInstance();
        for (Field f : fields) {
            map.put(f.getName(), FieldGeneratorFactory.generateFieldValue(f));
        }
        return map;
    }

    @Override
    public Class<Map> getCastTo() {
        return type;
    }

    @Override
    public Map createContainerInstance() {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return Utils.rethrowRuntimeException(e);
        }
    }
}
