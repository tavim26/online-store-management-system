package io.github.tavim26.onlinestore.dao;

import io.github.tavim26.onlinestore.model.PaymentCard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentCardDao {

    public int insert(Connection c, PaymentCard card) throws SQLException {
        String sql = "INSERT INTO payment_cards (user_id, card_type, last_four, expiry_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, card.getUserId());
            ps.setString(2, card.getCardType());
            ps.setString(3, card.getLastFour());
            ps.setObject(4, card.getExpiryDate());
            ps.executeUpdate();
            int id = JdbcUtils.generatedId(ps);
            card.setId(id);
            return id;
        }
    }

    public List<PaymentCard> findByUser(Connection c, int userId) throws SQLException {
        String sql = "SELECT * FROM payment_cards WHERE user_id = ? ORDER BY expiry_date";
        List<PaymentCard> cards = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cards.add(map(rs));
                }
            }
        }
        return cards;
    }

    public boolean delete(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM payment_cards WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private static PaymentCard map(ResultSet rs) throws SQLException {
        return new PaymentCard(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("card_type"),
                rs.getString("last_four"),
                rs.getObject("expiry_date", LocalDate.class));
    }
}