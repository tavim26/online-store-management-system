package io.github.tavim26.onlinestore.config;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/** Unchecked wrapper for technical database errors. */
public class DataAccessException extends RuntimeException {

    public DataAccessException(SQLException cause) {
        super("Database error: " + cause.getMessage(), cause);
    }

    /** True if the database rejected the data because of a UNIQUE or FOREIGN KEY constraint. */
    public boolean isConstraintViolation() {
        return getCause() instanceof SQLIntegrityConstraintViolationException;
    }
}