//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rh.heji.data.db.mongo;

public final class Assertions {
    public static <T> T notNull(String name, T value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " can not be null");
        } else {
            return value;
        }
    }

    public static void isTrue(String name, boolean condition) {
        if (!condition) {
            throw new IllegalStateException("state should be: " + name);
        }
    }

    public static void isTrueArgument(String name, boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("state should be: " + name);
        }
    }

    public static <T> T isTrueArgument(String name, T value, boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("state should be: " + name);
        } else {
            return value;
        }
    }

    public static <T> T convertToType(Class<T> clazz, Object value, String errorMessage) {
        if (!clazz.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(errorMessage);
        } else {
            return (T) value;
        }
    }

    private Assertions() {
    }
}
