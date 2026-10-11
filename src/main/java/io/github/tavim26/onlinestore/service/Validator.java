package io.github.tavim26.onlinestore.service;

import java.util.ArrayList;
import java.util.List;

/** Collects validation errors and throws them all at once. */
final class Validator {

    private final List<String> errors = new ArrayList<>();

    Validator check(boolean condition, String message) {
        if (!condition) {
            errors.add(message);
        }
        return this;
    }

    void throwIfInvalid() {
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}