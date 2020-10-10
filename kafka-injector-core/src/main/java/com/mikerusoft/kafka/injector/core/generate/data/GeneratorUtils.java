package com.mikerusoft.kafka.injector.core.generate.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

class GeneratorUtils {

    private GeneratorUtils() {}

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    static Functions functions(Class<?> type) {
        return functions.get(type);
    }

    private static final Map<Class<?>, Functions> functions = new ConcurrentHashMap<>();
    static {
        GeneratorUtils.put(
            Functions.<Number>builder()
                .random(r -> Math.abs(r.nextInt()))
                .mod((v, mod) -> v.intValue() % mod.intValue())
                .comparator(Comparator.comparingInt(o -> (int) o))
                .add((i1, i2) -> i1.intValue() + i2.intValue())
                .subtract((i1, i2) -> i1.intValue() - i2.intValue())
                .parser(Integer::valueOf)
                .divide((i1,i2) -> i1.intValue() / i2.intValue())
                .build(),
            Integer.class, Integer.TYPE
        );
        GeneratorUtils.put(
            GeneratorUtils.Functions.<Number>builder()
                .random(r -> Math.abs(r.nextLong()))
                .comparator(Comparator.comparingLong(o -> (long) o))
                .mod((v, mod) -> v.longValue() % mod.longValue())
                .add((i1, i2) -> i1.longValue() + i2.longValue())
                .subtract((i1, i2) -> i1.longValue() - i2.longValue())
                .divide((i1,i2) -> i1.longValue() / i2.longValue())
                .parser(Long::valueOf)
                .build(),
            Long.class, Long.TYPE
        );
        GeneratorUtils.put(
            GeneratorUtils.Functions.<Number>builder()
                .random(r -> ((Integer)Math.abs(r.nextInt(Short.MAX_VALUE))).shortValue())
                .comparator(Comparator.comparingInt(o -> (short) o))
                .mod((v, mod) -> v.shortValue() % mod.shortValue())
                .add((i1, i2) -> (short)(i1.shortValue() + i2.shortValue()))
                .subtract((i1, i2) -> (short)(i1.shortValue() - i2.shortValue()))
                .divide((i1,i2) -> i1.shortValue() / i2.shortValue())
                .parser(Short::valueOf)
                .build(),
            Short.class, Short.TYPE
        );
        GeneratorUtils.put(
            GeneratorUtils.Functions.<Number>builder()
                .random(r -> ((Integer)Math.abs(r.nextInt(Byte.MAX_VALUE))).byteValue())
                .comparator(Comparator.comparingInt(o -> (byte) o))
                .mod((v, mod) -> v.byteValue() % mod.byteValue())
                .add((i1, i2) -> (byte)(i1.byteValue() + i2.byteValue()))
                .subtract((i1, i2) -> (byte)(i1.byteValue() - i2.byteValue()))
                .divide((i1,i2) -> i1.byteValue() / i2.byteValue())
                .parser(Byte::valueOf)
                .build(),
            Byte.class, Byte.TYPE
        );
        GeneratorUtils.put(
            GeneratorUtils.Functions.<Number>builder()
                .random(Random::nextDouble)
                .comparator(Comparator.comparingDouble(o -> (double) o))
                .add((i1, i2) -> i1.doubleValue() + i2.doubleValue())
                .subtract((i1, i2) -> i1.doubleValue() - i2.doubleValue())
                .parser(Double::valueOf)
                .build(),
            Double.class, Double.TYPE
        );
        GeneratorUtils.put(
            GeneratorUtils.Functions.<Number>builder()
                .random(Random::nextFloat)
                .comparator(Comparator.comparingDouble(o -> (float) o))
                .add((i1, i2) -> i1.floatValue() + i2.floatValue())
                .subtract((i1, i2) -> i1.floatValue() - i2.floatValue())
                .parser(Float::valueOf)
                .build(),
            Float.class, Float.TYPE
        );
        GeneratorUtils.put(
            GeneratorUtils.Functions.<Boolean>builder()
                .random(Random::nextBoolean)
                .comparator((o1, o2) -> {
                    if (o1 == o2)
                        return 0;
                    return o1 ? -1 : 1;
                })
                .add((i1, i2) -> i1 && i2)
                .parser(Boolean::valueOf)
                .build(),
            Boolean.class, Boolean.TYPE
        );
        GeneratorUtils.put(
            GeneratorUtils.Functions.<String>builder()
                .comparator((o1, o2) -> o1 == null ? -1 : o1.compareTo(o2))
                .add((i1, i2) -> i1 + i2)
                .parser(s -> s)
                .build(),
            String.class
        );
    }

    private static void put(Functions functions, Class<?>... keys) {
        Stream.of(Optional.ofNullable(keys).orElse(new Class<?>[0])).forEach(key -> GeneratorUtils.functions.put(key, functions));
    }

    public static boolean isIntegerNumberType(Class<? extends Number> type) {
        return Stream.of(Integer.class, Integer.TYPE, Long.class, Long.TYPE, Short.class, Short.TYPE, Byte.class, Byte.TYPE)
            .anyMatch(t -> t.isAssignableFrom(type));
    }

    public static boolean isFloatingNumberType(Class<? extends Number> type) {
        return Stream.of(Double.class, Double.TYPE, Float.class, Float.TYPE)
            .anyMatch(t -> t.isAssignableFrom(type));
    }

    public static long getDate(String date, String pattern) {
        return LocalDateTime.parse(
            date,
            DateTimeFormatter.ofPattern(pattern, Locale.US)
        ).atZone(
            ZoneId.of(TimeZone.getDefault().getID())
        ).toInstant().toEpochMilli();
    }

    public static void assertValidNumericType(Class<?> type) {
        assertClass(type);
        assertClassIsNumber(type);
    }

    public static void assertValidIntType(Class<?> type) {
        assertClass(type);
        assertClassIsNumber(type);
        if (!GeneratorUtils.isIntegerNumberType((Class<Number>)type)) {
            throw new IllegalArgumentException("Class doesn't one ofDuration integer types in java. Class is " + type.getName());
        }
    }

    private static void assertClass(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Class couldn't be null");
        }
    }

    static void assertValidFloatingType(Class<?> type) {
        assertClass(type);
        assertClassIsNumber(type);
        if (!GeneratorUtils.isFloatingNumberType((Class<Number>)type)) {
            throw new IllegalArgumentException("Class isn't one ofDuration float types in java. Class is " + type.getName());
        }
    }

    private static void assertClassIsNumber(Class<?> type) {
        if (!Number.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Class doesn't extends Number. Class is " + type.getName());
        }
    }

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    static class Functions<T> {
        Function<Random, T> random;
        Comparator<T> comparator;
        BiFunction<T, T, T> add;
        BiFunction<T, T, T> subtract;
        Function<String, T> parser;
        BiFunction<T, T, T> mod;
        BiFunction<T, T, T> divide;

        public T random(Random random) { return this.random.apply(random); }
        public T mod(T value, T modeFactor) { return this.mod.apply(value, modeFactor); }
        public T add(T t1, T t2) { return this.add.apply(t1, t2); }
        public T subtract(T t1, T t2) { return this.subtract.apply(t1, t2); }
        public T parse(String str) { return this.parser.apply(str); }
        public int compare(T o1, T o2) { return this.comparator.compare(o1, o2); }
        public T divide(T o1, T o2) { return this.divide.apply(o1, o2); }
    }
}
