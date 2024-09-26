package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/store";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    private static Connection connection;

    public static Connection getConnection() throws SQLException
    {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                throw new SQLException("Driver JDBC not found", e);
            }
        }
        return connection;
    }

    public static void closeConnection() throws SQLException
    {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }



    public static void main(String[] args) {
        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();

            if (connection != null && !connection.isClosed()) {
                System.out.println("Success!");
            } else {
                System.out.println("Failed");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
