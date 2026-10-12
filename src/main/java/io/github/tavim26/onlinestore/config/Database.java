package io.github.tavim26.onlinestore.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Database {

    /** A piece of database work that receives an open connection. */
    @FunctionalInterface
    public interface SqlWork<T> {
        T execute(Connection connection) throws SQLException;
    }

    private static final Path CONFIG_FILE = Path.of("db.properties");
    private static final Properties CONFIG = loadConfig();

    private Database() {
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Missing " + CONFIG_FILE.toAbsolutePath()
                            + ". Copy db.properties.example to db.properties and fill in your credentials.", e);
        }
        return props;
    }

    /** Opens a new connection. The caller is responsible for closing it. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.password"));
    }

    /** Runs the work on a fresh connection that is always closed afterwards. */
    public static <T> T withConnection(SqlWork<T> work) {
        try (Connection connection = getConnection()) {
            return work.execute(connection);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Runs the work in a single transaction: commit if everything succeeds,
     * rollback if any SQLException or RuntimeException is thrown.
     */
    public static <T> T inTransaction(SqlWork<T> work) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                T result = work.execute(connection);
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}