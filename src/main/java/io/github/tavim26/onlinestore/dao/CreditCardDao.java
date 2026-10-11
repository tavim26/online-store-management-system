package io.github.tavim26.onlinestore.dao;

import io.github.tavim26.onlinestore.model.CreditCard;
import io.github.tavim26.onlinestore.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreditCardDao extends AbstractDao<CreditCard> {

    @Override
    public void create(CreditCard creditCard) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "INSERT INTO CreditCards (UserID, CreditCardNumber, CreditCardType, CVV, CardBalance, ExpirationDate) VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, creditCard.getUser().getUserID());
            preparedStatement.setString(2, creditCard.getCreditCardNumber());
            preparedStatement.setString(3, creditCard.getCreditCardType());
            preparedStatement.setString(4, creditCard.getCvv());
            preparedStatement.setDouble(5, creditCard.getCardBalance());
            preparedStatement.setDate(6, Date.valueOf(creditCard.getExpirationDate()));
            preparedStatement.executeUpdate();

            // Get the generated credit card ID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int creditCardId = generatedKeys.getInt(1);
                creditCard.setCreditCardID(creditCardId);
            }
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public CreditCard read(int id) throws SQLException {
        CreditCard creditCard = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM CreditCards WHERE CreditCardID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = getUserById(resultSet.getInt("UserID"));
                creditCard = new CreditCard(
                        resultSet.getInt("CreditCardID"),
                        user,
                        resultSet.getString("CreditCardNumber"),
                        resultSet.getString("CreditCardType"),
                        resultSet.getString("CVV"),
                        resultSet.getDouble("CardBalance"),
                        resultSet.getDate("ExpirationDate").toLocalDate()
                );
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return creditCard;
    }

    private User getUserById(int userId) {
        // Implement logic to retrieve User by ID
        return null; // Placeholder
    }

    @Override
    public void update(CreditCard creditCard) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "UPDATE CreditCards SET UserID = ?, CreditCardNumber = ?, CreditCardType = ?, CVV = ?, CardBalance = ?, ExpirationDate = ? WHERE CreditCardID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, creditCard.getUser().getUserID());
            preparedStatement.setString(2, creditCard.getCreditCardNumber());
            preparedStatement.setString(3, creditCard.getCreditCardType());
            preparedStatement.setString(4, creditCard.getCvv());
            preparedStatement.setDouble(5, creditCard.getCardBalance());
            preparedStatement.setDate(6, Date.valueOf(creditCard.getExpirationDate()));
            preparedStatement.setInt(7, creditCard.getCreditCardID());
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "DELETE FROM CreditCards WHERE CreditCardID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public List<CreditCard> readAll() throws SQLException {
        List<CreditCard> creditCards = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM CreditCards";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CreditCard creditCard = read(resultSet.getInt("CreditCardID")); // Reuse read method
                creditCards.add(creditCard);
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return creditCards;
    }
}
