package io.github.tavim26.onlinestore.dao;


import io.github.tavim26.onlinestore.model.Order;
import io.github.tavim26.onlinestore.model.Product;
import io.github.tavim26.onlinestore.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends AbstractDao<Order> {

    @Override
    public void create(Order order) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "INSERT INTO Orders (UserID, TotalPrice, OrderDate, OrderStatus, ShippingAddress) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, order.getUser().getUserID());
            preparedStatement.setDouble(2, order.getTotalPriece());
            preparedStatement.setDate(3, Date.valueOf(order.getOrderDate()));
            preparedStatement.setString(4, order.getOrderStatus());
            preparedStatement.setString(5, order.getShippingAddress());
            preparedStatement.executeUpdate();

            // Get the generated order ID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);
                order.setOrderId(orderId);
            }

            // Save order products
            for (Product product : order.getProducts()) {
                // Assuming you have a method to save the order-product relationship
                saveOrderProduct(order.getOrderId(), product.getProductID(), product.getProductQuantity());
            }
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    private void saveOrderProduct(int orderId, int productId, int quantity) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "INSERT INTO OrderProducts (OrderID, ProductID, Quantity) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, quantity);
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public Order read(int id) throws SQLException {
        Order order = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM Orders WHERE OrderID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("UserID");
                double totalPrice = resultSet.getDouble("TotalPrice");
                LocalDate orderDate = resultSet.getDate("OrderDate").toLocalDate();
                String orderStatus = resultSet.getString("OrderStatus");
                String shippingAddress = resultSet.getString("ShippingAddress");

                // Fetch user and products
                User user = getUserById(userId);
                List<Product> products = getProductsByOrderId(id);

                order = new Order(
                        id,
                        user,
                        products,
                        products.size(), // Assuming quantity is the count of products
                        totalPrice,
                        orderDate,
                        orderStatus,
                        shippingAddress
                );
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return order;
    }

    private User getUserById(int userId) {
        // Fetch user by ID logic
        return null; // Placeholder
    }

    private List<Product> getProductsByOrderId(int orderId) {
        List<Product> products = new ArrayList<>();
        // Logic to fetch products based on orderId
        return products; // Placeholder
    }

    @Override
    public void update(Order order) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "UPDATE Orders SET UserID = ?, TotalPrice = ?, OrderDate = ?, OrderStatus = ?, ShippingAddress = ? WHERE OrderID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, order.getUser().getUserID());
            preparedStatement.setDouble(2, order.getTotalPriece());
            preparedStatement.setDate(3, Date.valueOf(order.getOrderDate()));
            preparedStatement.setString(4, order.getOrderStatus());
            preparedStatement.setString(5, order.getShippingAddress());
            preparedStatement.setInt(6, order.getOrderId());
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
            String query = "DELETE FROM Orders WHERE OrderID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public List<Order> readAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM Orders";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Order order = read(resultSet.getInt("OrderID")); // Reuse read method
                orders.add(order);
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return orders;
    }
}

