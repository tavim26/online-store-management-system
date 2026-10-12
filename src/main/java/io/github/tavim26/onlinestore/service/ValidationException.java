package io.github.tavim26.onlinestore.service;

import java.util.List;

/** Invalid input. Holds all the problems found, so a form can show them together. */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join("\n", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}