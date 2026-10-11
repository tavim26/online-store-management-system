package io.github.tavim26.onlinestore.dao;

import io.github.tavim26.onlinestore.model.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class ReviewDao {

    public int insert(Connection c, Review review) throws SQLException {
        String sql = "INSERT INTO reviews (user_id, product_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, review.getUserId());
            ps.setInt(2, review.getProductId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.executeUpdate();
            int id = JdbcUtils.generatedId(ps);
            review.setId(id);
            return id;
        }
    }

    public List<Review> findByProduct(Connection c, int productId) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE product_id = ? ORDER BY created_at DESC";
        List<Review> reviews = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(map(rs));
                }
            }
        }
        return reviews;
    }

    /** Average rating of a product, or empty if it has no reviews. */
    public OptionalDouble averageRating(Connection c, int productId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT AVG(rating) FROM reviews WHERE product_id = ?")) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                double average = rs.getDouble(1);
                return rs.wasNull() ? OptionalDouble.empty() : OptionalDouble.of(average);
            }
        }
    }

    public boolean update(Connection c, Review review) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE reviews SET rating = ?, comment = ? WHERE id = ?")) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setInt(3, review.getId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM reviews WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private static Review map(ResultSet rs) throws SQLException {
        return new Review(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("product_id"),
                rs.getInt("rating"),
                rs.getString("comment"),
                rs.getObject("created_at", LocalDateTime.class));
    }
}