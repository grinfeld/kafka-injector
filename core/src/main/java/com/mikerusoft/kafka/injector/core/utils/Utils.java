package com.mikerusoft.kafka.injector.core.utils;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofNanos;

public class Utils {

    private static final BigInteger BI_NANOS_PER_MICRO = BigInteger.valueOf(1000L);
    private static final BigInteger BI_NANOS_PER_MILLI = BigInteger.valueOf(1000000L);
    private static final BigInteger BI_NANOS_PER_SECOND = BigInteger.valueOf(1000000000L);
    private static final BigInteger BI_NANOS_PER_MINUTE = BigInteger.valueOf(60L * 1000000000L);
    private static final BigInteger BI_NANOS_PER_HOUR = BigInteger.valueOf(60L * 60L * 1000000000L);
    private static final BigInteger BI_NANOS_PER_DAY = BigInteger.valueOf(24L * 60L * 60L * 1000000000L);

    public static void rethrowRuntimeException(Exception e) throws RuntimeException {
        if (e instanceof RuntimeException)
            throw (RuntimeException)e;
        throw new RuntimeException(e);
    }

    public static String toString(byte[] arr) {
        if (arr == null)
            return "null";
        if (arr.length == 0)
            return "[]";

        StringBuilder str = new StringBuilder("[");
        for (int i=0; i<arr.length; i++) {
             str.append(i==0 ? "" : ",").append(arr[i]);
        }
        str.append("]");

        return str.toString();
    }

    public static <T> T[] createArray(Class<T> type, int size) {
        //noinspection unchecked
        return (T[])Array.newInstance(type, size);
    }

    public static <T> T deNull(T t, T def) {
        return t == null ? def : t;
    }

    public static boolean isEmpty(String str) {
        return "".equals(deNull(str, ""));
    }

    // in jdk9 we don't need this
    public static Duration ofDuration(long amount, TimeUnit unit) {
        long nanos = unit.toNanos(amount);
        if (unit == TimeUnit.NANOSECONDS) {
            return ofNanos(nanos);
        }
        BigInteger calc = BigInteger.valueOf(amount);
        switch (unit) {
            case MICROSECONDS:
                return ofNanos(calc.multiply(BI_NANOS_PER_MICRO).longValue());
            case MILLISECONDS:
                return ofNanos(calc.multiply(BI_NANOS_PER_MILLI).longValue());
            case SECONDS:
                return ofNanos(calc.multiply(BI_NANOS_PER_SECOND).longValue());
            case MINUTES:
                return ofNanos(calc.multiply(BI_NANOS_PER_MINUTE).longValue());
            case HOURS:
                return ofNanos(calc.multiply(BI_NANOS_PER_HOUR).longValue());
            case DAYS:
                return ofNanos(calc.multiply(BI_NANOS_PER_DAY).longValue());
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    public static void notNull(Object obj, String msg) {
        if (obj == null)
            throw new NullPointerException(msg);
    }
}
