package com.mikerusoft.kafka.injector.core.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflections {

    private Reflections() {}

    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentHashMap<>(256);
    private static final Method[] NO_METHODS = {};
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

    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        Utils.notNull(clazz, "Class must not be null");
        Utils.notNull(name, "Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
            for (Method method : methods) {
                if (name.equals(method.getName()) &&
                        (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new LinkedList<>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

    private static Method[] getDeclaredMethods(Class<?> clazz) {
        Utils.notNull(clazz, "Class must not be null");
        Method[] result = declaredMethodsCache.get(clazz);
        if (result == null) {
            try {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
                if (defaultMethods != null) {
                    result = new Method[declaredMethods.length + defaultMethods.size()];
                    System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                    int index = declaredMethods.length;
                    for (Method defaultMethod : defaultMethods) {
                        result[index] = defaultMethod;
                        index++;
                    }
                }
                else {
                    result = declaredMethods;
                }
                declaredMethodsCache.put(clazz, (result.length == 0 ? NO_METHODS : result));
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                        "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

    public static Method findConsumerMethod(Class<?> type, String name, Class<?> param) {
        Method method = methodCache.get(type.getName() + "_" + name);
        if (method == null) {
            method = findMethod(type, name, param);
            if (method == null) {
                // maybe method defined with primitive parameter, so let's try with primitive one
                method = findMethod(type, name, getPrimitiveType(param));
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
