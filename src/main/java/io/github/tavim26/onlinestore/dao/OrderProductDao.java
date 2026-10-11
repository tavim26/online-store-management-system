package io.github.tavim26.onlinestore.dao;

import io.github.tavim26.onlinestore.model.Order;
import io.github.tavim26.onlinestore.model.OrderProduct;
import io.github.tavim26.onlinestore.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderProductDao extends AbstractDao<OrderProduct> {

    @Override
    public void create(OrderProduct orderProduct) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "INSERT INTO OrderProducts (OrderID, ProductID, Quantity) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderProduct.getOrder().getOrderId());
            preparedStatement.setInt(2, orderProduct.getProduct().getProductID());
            preparedStatement.setInt(3, orderProduct.getQuantity());
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public OrderProduct read(int id) throws SQLException {
        OrderProduct orderProduct = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM OrderProducts WHERE OrderProductID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Order order = getOrderById(resultSet.getInt("OrderID"));
                Product product = getProductById(resultSet.getInt("ProductID"));
                int quantity = resultSet.getInt("Quantity");
                orderProduct = new OrderProduct(order, product, quantity);
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return orderProduct;
    }

    private Order getOrderById(int orderId) {
        // Implement logic to retrieve Order by ID
        return null; // Placeholder
    }

    private Product getProductById(int productId) {
        // Implement logic to retrieve Product by ID
        return null; // Placeholder
    }

    @Override
    public void update(OrderProduct orderProduct) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "UPDATE OrderProducts SET Quantity = ? WHERE OrderID = ? AND ProductID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderProduct.getQuantity());
            preparedStatement.setInt(2, orderProduct.getOrder().getOrderId());
            preparedStatement.setInt(3, orderProduct.getProduct().getProductID());
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
            String query = "DELETE FROM OrderProducts WHERE OrderProductID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public List<OrderProduct> readAll() throws SQLException {
        List<OrderProduct> orderProducts = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM OrderProducts";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Order order = getOrderById(resultSet.getInt("OrderID"));
                Product product = getProductById(resultSet.getInt("ProductID"));
                int quantity = resultSet.getInt("Quantity");
                OrderProduct orderProduct = new OrderProduct(order, product, quantity);
                orderProducts.add(orderProduct);
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return orderProducts;
    }
}

