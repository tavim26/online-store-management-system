package io.github.tavim26.onlinestore.dao;

import io.github.tavim26.onlinestore.model.Wishlist;
import io.github.tavim26.onlinestore.model.User;
import io.github.tavim26.onlinestore.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDao extends AbstractDao<Wishlist> {

    @Override
    public void create(Wishlist wishlist) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "INSERT INTO Wishlists (UserID) VALUES (?)";
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, wishlist.getUser().getUserID());
            preparedStatement.executeUpdate();

            // Get the generated wishlist ID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int wishListId = generatedKeys.getInt(1);
                wishlist.setWishListID(wishListId);
            }

            // Now add products to the wishlist
            for (Product product : wishlist.getProducts()) {
                addProductToWishlist(wishlist.getWishListID(), product.getProductID(), connection);
            }
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    private void addProductToWishlist(int wishListId, int productId, Connection connection) throws SQLException {
        String query = "INSERT INTO WishlistProducts (WishListID, ProductID) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, wishListId);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Wishlist read(int id) throws SQLException {
        Wishlist wishlist = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM Wishlists WHERE WishListID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("UserID");
                User user = getUserById(userId);

                // Now get the products in the wishlist
                List<Product> products = getProductsInWishlist(id, connection);

                wishlist = new Wishlist(id, user, products);
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return wishlist;
    }

    private List<Product> getProductsInWishlist(int wishListId, Connection connection) throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.* FROM Products p " +
                "JOIN WishlistProducts wp ON p.ProductID = wp.ProductID " +
                "WHERE wp.WishListID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, wishListId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("ProductID"),
                        resultSet.getString("ProductName"),
                        resultSet.getString("ProductDescription"),
                        resultSet.getDouble("ProductPrice"),
                        resultSet.getInt("ProductQuantity"),
                        resultSet.getBoolean("IsAvailable")
                );
                products.add(product);
            }
        }
        return products;
    }

    private User getUserById(int userId) {
        // Logic to fetch user by ID
        return null; // Placeholder
    }

    @Override
    public void update(Wishlist wishlist) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "UPDATE Wishlists SET UserID = ? WHERE WishListID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, wishlist.getUser().getUserID());
            preparedStatement.setInt(2, wishlist.getWishListID());
            preparedStatement.executeUpdate();

            // Update products in the wishlist (optional)
            // This can be implemented based on the specific requirements
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
            String query = "DELETE FROM WishlistProducts WHERE WishListID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            // Now delete the wishlist itself
            String deleteWishlistQuery = "DELETE FROM Wishlists WHERE WishListID = ?";
            preparedStatement = connection.prepareStatement(deleteWishlistQuery);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public List<Wishlist> readAll() throws SQLException {
        List<Wishlist> wishlists = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM Wishlists";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Wishlist wishlist = read(resultSet.getInt("WishListID")); // Reuse read method
                wishlists.add(wishlist);
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return wishlists;
    }
}
