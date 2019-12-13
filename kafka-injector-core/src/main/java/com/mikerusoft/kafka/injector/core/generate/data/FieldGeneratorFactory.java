package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.properties.Field;
import com.mikerusoft.kafka.injector.core.utils.Reflections;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FieldGeneratorFactory {

    private FieldGeneratorFactory() {}

    static ValueGenerator<?> valueGenerator(Field field) {
        final Class<?> castTo = Reflections.getCustomCastTo(field.getCast());
        ValueGenerator<?> fieldGenerator = null;
        switch (field.getType()) {
            case SEQUENTIAL_RANGE:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(SequentialRangeGenerator.class, field.getCast().getName(), field.getValue(), field.getUid()),
                    () -> new SequentialRangeGenerator<>(field.getValue(), castTo)
                );
                break;

            case TIMESTAMP_REGEX:
                fieldGenerator = getOrInitValueGenerator(generateStringKey(RegexTimestampGenerator.class, field.getValue(), field.getUid()), () -> new RegexTimestampGenerator(field.getValue()));
                break;
            case TIMESTAMP:
                fieldGenerator = getOrInitValueGenerator(generateStringKey(TimestampGenerator.class, field.getValue(), field.getUid()), () -> new TimestampGenerator(field.getValue()));
                break;
            case EMPTY:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(FixedValueGenerator.class, field.getCast().getName()),
                    () -> new FixedValueGenerator<>("", String.class)
                );
                break;
            case NIL:
                fieldGenerator = getOrInitValueGenerator(generateStringKey(TimestampGenerator.class), NullValueGenerator::new);
                break;
            case REGEX:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(RegexGenerator.class, field.getCast().getName(), field.getValue(), field.getUid()),
                    () -> new RegexGenerator<>(castTo, field.getValue())
                );
                break;
            case FIXED:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(FixedValueGenerator.class, field.getCast().getName(), field.getValue(), field.getUid()),
                    () -> new FixedValueGenerator<>(field.getValue(), castTo)
                );
                break;
            case RANDOM:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(RandomPrimitiveValueGenerated.class, field.getCast().getName(), field.getUid()),
                    () -> new RandomPrimitiveValueGenerated(castTo));
                break;
            case RANGE:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(PrimitiveNumberRangeGenerator.class, field.getCast().getName(), field.getValue(), field.getUid()),
                    () -> PrimitiveNumberRangeGenerator.factory(castTo, field.getValue())
                );
                break;
            case LIST:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(RandomPrimitiveValueGenerated.class, field.getCast().getName(), field.getValue(), field.getUid()),
                    () -> new ValueFromListGenerator<>(castTo, field.getValue())
                );
                break;
            case RANDOM_LIST:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(RandomPrimitiveValueGenerated.class, field.getCast().getName(), field.getValue(), field.getUid()),
                    () -> new RandomValueFromValueFromList<>(castTo, field.getValue())
                );
                break;
            case NESTED_LIST:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(NestedListGenerator.class, List.class.getName() + field.getCast().getName(), String.valueOf(field.getValue()) + Objects.hash(field.getNestedFields()), field.getUid()),
                    () -> new NestedListGenerator<>(field.getNestedFields(), field.getCast())
                );
                break;
            case NESTED_OBJECT:
                fieldGenerator = getOrInitValueGenerator(
                    generateStringKey(NestedObjectGenerator.class, List.class.getName() + field.getCast().getName(), String.valueOf(field.getValue()) + Objects.hash(field.getNestedFields()), field.getUid()),
                    () -> new NestedObjectGenerator<>(field.getNestedFields(), field.getCast())
                );
                break;
            default:
                throw new RuntimeException("Unknown Type");
        }
        return fieldGenerator;
    }

    public static Object generateFieldValue(Field field) {
        ValueGenerator<?> valueGenerator = valueGenerator(field);
        return Reflections.customCast(valueGenerator.generate(), valueGenerator.getCastTo());
    }

    private static ValueGenerator<?> getOrInitValueGenerator(String key, Supplier<ValueGenerator<?>> initGenerator) {
        ValueGenerator<?> valueGenerator = generators.get(key);
        if (valueGenerator == null) {
            valueGenerator = initGenerator.get();
            ValueGenerator<?> existed = generators.putIfAbsent(key, valueGenerator);
            if (existed != null)
                valueGenerator = existed;
            log.debug("Generating with key " + key + " -> " + valueGenerator.getClass().getName());
        }
        return valueGenerator;
    }

    private static String generateStringKey(Class<?> clazz, String...others) {
        return clazz.getName() + (
            Stream.of(Optional.of(others).orElseGet(() -> new String[0]))
                    .filter(Objects::nonNull).collect(Collectors.joining("_", "_", ""))
        );
    }

    private static Map<String, ValueGenerator<?>> generators = new ConcurrentHashMap<>();
}
