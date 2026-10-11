package io.github.tavim26.onlinestore.ui;

import io.github.tavim26.onlinestore.config.DataAccessException;
import io.github.tavim26.onlinestore.service.BusinessException;
import io.github.tavim26.onlinestore.service.ValidationException;

/** Turns exceptions into text that can be shown to the user. */
public final class ErrorMessages {

    private static final System.Logger LOG = System.getLogger(ErrorMessages.class.getName());

    private ErrorMessages() {
    }

    public static String of(Throwable error) {
        if (error instanceof ValidationException validation) {
            return String.join("\n", validation.getErrors());
        }
        if (error instanceof BusinessException) {
            return error.getMessage();
        }
        LOG.log(System.Logger.Level.ERROR, "Unexpected error", error);
        if (error instanceof DataAccessException) {
            return "Could not reach the database. Please try again later.";
        }
        return "Something went wrong. Please try again.";
    }
}