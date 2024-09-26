package Database;


import Model.Product;
import Model.Wishlist;
import Model.WishlistProduct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistProductDAO extends AbstractDAO<WishlistProduct> {

    @Override
    public void create(WishlistProduct wishlistProduct) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "INSERT INTO WishlistProducts (WishListID, ProductID) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, wishlistProduct.getWishlist().getWishListID());
            preparedStatement.setInt(2, wishlistProduct.getProduct().getProductID());
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public WishlistProduct read(int id) throws SQLException {
        WishlistProduct wishlistProduct = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM WishlistProducts WHERE WishListProductID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Wishlist wishlist = getWishlistById(resultSet.getInt("WishListID"));
                Product product = getProductById(resultSet.getInt("ProductID"));
                wishlistProduct = new WishlistProduct(wishlist, product);
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return wishlistProduct;
    }

    private Wishlist getWishlistById(int wishlistId) {
        // Implement logic to retrieve Wishlist by ID
        return null; // Placeholder
    }

    private Product getProductById(int productId) {
        // Implement logic to retrieve Product by ID
        return null; // Placeholder
    }

    @Override
    public void update(WishlistProduct wishlistProduct) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String query = "DELETE FROM WishlistProducts WHERE WishListProductID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection();
        }
    }

    @Override
    public List<WishlistProduct> readAll() throws SQLException {
        List<WishlistProduct> wishlistProducts = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM WishlistProducts";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Wishlist wishlist = getWishlistById(resultSet.getInt("WishListID"));
                Product product = getProductById(resultSet.getInt("ProductID"));
                WishlistProduct wishlistProduct = new WishlistProduct(wishlist, product);
                wishlistProducts.add(wishlistProduct);
            }
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection();
        }

        return wishlistProducts;
    }
}
