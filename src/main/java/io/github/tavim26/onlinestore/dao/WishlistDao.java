package io.github.tavim26.onlinestore.dao;

import io.github.tavim26.onlinestore.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishlistDao {

    /** Returns true if the product was added, false if it was already in the wishlist. */
    public boolean add(Connection c, int userId, int productId) throws SQLException {
        String sql = "INSERT IGNORE INTO wishlist_items (user_id, product_id) VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean remove(Connection c, int userId, int productId) throws SQLException {
        String sql = "DELETE FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Product> findProducts(Connection c, int userId) throws SQLException {
        String sql = "SELECT p.* FROM products p "
                + "JOIN wishlist_items w ON w.product_id = p.id "
                + "WHERE w.user_id = ? ORDER BY w.added_at DESC";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(ProductDao.map(rs));
                }
            }
        }
        return products;
    }
}