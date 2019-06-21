package com.mikerusoft.kafka.injector.core.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflections {
    private static final Map<Class<?>, Class<?>> primitives = new ConcurrentHashMap<>();
    static {
        Reflections.primitives.put(Integer.class, Integer.TYPE);
        Reflections.primitives.put(Long.class, Long.TYPE);
        Reflections.primitives.put(Short.class, Short.TYPE);
        Reflections.primitives.put(Byte.class, Byte.TYPE);
        Reflections.primitives.put(Double.class, Double.TYPE);
        Reflections.primitives.put(Float.class, Float.TYPE);
        Reflections.primitives.put(Boolean.class, Boolean.TYPE);
    }

    private static final Map<String, Method> methodCache = new ConcurrentHashMap<>();

    public static Method findConsumerMethod(Class<?> type, String name, Class<?> param) {
        Method method = methodCache.get(type.getName() + "_" + name);
        if (method == null) {
            method = ReflectionUtils.findMethod(type, name, param);
            if (method == null) {
                // maybe method defined with primitive parameter, so let's try with primitive one
                method = ReflectionUtils.findMethod(type, name, getPrimitiveType(param));
            }

            if (method == null) {
                List<Method> candidates = findCandidates(type, name, param);
                if (!candidates.isEmpty()) {
                    method = candidates.get(0); // taking the first one
                }
            }

            if (method == null) {
                throw new NullPointerException("Failed to find method " + name);
            }
            methodCache.put(type.getName() + "_" + name, method);
        }
        return method;
    }

    private static List<Method> findCandidates(Class<?> type, String name, Class<?> param) {
        return Stream.of(type.getDeclaredMethods())
                .filter(m -> Objects.equals(m.getName(), name))
                .filter(m -> m.getParameterTypes().length > 0)
                .filter(m -> m.getGenericParameterTypes().length == 1)
                .filter(m -> m.getParameterTypes()[0].isAssignableFrom(param))
                .collect(Collectors.toList());
    }

    private static Class getPrimitiveType(Class type) {
        return Optional.ofNullable(primitives.get(type)).orElse(type);
    }

    public static Class<?> getCustomCastTo(Class castTo) {
        return Optional.ofNullable(converters.get(castTo)).map(c -> c.getCastTo()).orElse(castTo);
    }

    // todo: should be changed to more appropriate way to work with custom classes
    public static Object customCast(Object obj, Class castTo) {
        return Optional.ofNullable(converters.get(castTo))
                .map(c -> c.castTo(obj)).orElseGet(() -> castTo.cast(obj));
    }

    private static Map<Class<?>, CustomCast> converters = new ConcurrentHashMap<>();
    static {
        converters.put(Integer.TYPE,
                CustomCast.builder().castTo(Integer.class)
                        .cast(v -> (int) v)
                        .build()
        );
        converters.put(Long.TYPE,
                CustomCast.builder().castTo(Long.class)
                        .cast(v -> (long) v)
                        .build()
        );
        converters.put(Short.TYPE,
                CustomCast.builder().castTo(Short.class)
                        .cast(v -> (short) v)
                        .build()
        );
        converters.put(Byte.TYPE,
                CustomCast.builder().castTo(Byte.class)
                        .cast(v -> (byte) v)
                        .build()
        );
        converters.put(Float.TYPE,
                CustomCast.builder().castTo(Float.class)
                        .cast(v -> (float) v)
                        .build()
        );
        converters.put(Double.TYPE,
                CustomCast.builder().castTo(Double.class)
                        .cast(v -> (double) v)
                        .build()
        );
    }

    @Data
    @AllArgsConstructor
    @Value
    @Builder(builderClassName = "builder")
    private static class CustomCast {
        private Class castTo;
        private Function<Object, Object> cast;

        private Object castTo(Object obj) {
            return this.cast.apply(obj);
        }
    }
}
