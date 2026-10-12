package io.github.tavim26.onlinestore.dao;

import io.github.tavim26.onlinestore.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDao {

    public int insert(Connection c, Product product) throws SQLException {
        String sql = "INSERT INTO products (name, description, price, stock) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.executeUpdate();
            int id = JdbcUtils.generatedId(ps);
            product.setId(id);
            return id;
        }
    }

    public Optional<Product> findById(Connection c, int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<Product> findAll(Connection c) throws SQLException {
        String sql = "SELECT * FROM products ORDER BY name";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(map(rs));
            }
        }
        return products;
    }

    public boolean update(Connection c, Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setInt(5, product.getId());
            return ps.executeUpdate() == 1;
        }
    }

    /** Atomically decreases the stock. Returns false if there is not enough stock. */
    public boolean decreaseStock(Connection c, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean increaseStock(Connection c, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM products WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /** Maps the current row to a Product. Also used by OrderDao and WishlistDao. */
    static Product map(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getInt("stock"));
    }
}