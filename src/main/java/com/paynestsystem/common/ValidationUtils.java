package com.paynestsystem.common;

import java.util.Objects;

/**
 * Shared validation helpers for simple domain invariants.
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static <T> T requireNonNull(T value, String fieldName) {
        return Objects.requireNonNull(value, fieldName + " must not be null");
    }

    public static String requireNonBlank(String value, String fieldName) {
        requireNonNull(value, fieldName);
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return trimmedValue;
    }

    public static double requireNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }
        return value;
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero");
        }
        return value;
    }
}
