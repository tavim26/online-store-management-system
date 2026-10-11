package io.github.tavim26.onlinestore.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class DatabaseConnection {

    private static final Path CONFIG_FILE = Path.of("db.properties");
    private static final Properties CONFIG = loadConfig();

    private static Connection connection;

    private DatabaseConnection() {
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

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    CONFIG.getProperty("db.url"),
                    CONFIG.getProperty("db.user"),
                    CONFIG.getProperty("db.password"));
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Temporary connectivity check; will be replaced by tests.
    public static void main(String[] args) throws SQLException {
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
            rs.next();
            System.out.println("Connected. Products in database: " + rs.getInt(1));
        }
    }
}