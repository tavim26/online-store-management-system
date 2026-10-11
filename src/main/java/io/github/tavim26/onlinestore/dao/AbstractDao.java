package io.github.tavim26.onlinestore.dao;


import io.github.tavim26.onlinestore.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractDao<T> {

    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    protected void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void closeConnection() {
        try {
            DatabaseConnection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CREATE
    public abstract void create(T entity) throws SQLException;

    // READ
    public abstract T read(int id) throws SQLException;

    // UPDATE
    public abstract void update(T entity) throws SQLException;

    // DELETE
    public abstract void delete(int id) throws SQLException;

    // READ ALL
    public abstract List<T> readAll() throws SQLException;
}

