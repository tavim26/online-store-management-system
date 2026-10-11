package io.github.tavim26.onlinestore.dao;

import io.github.tavim26.onlinestore.model.Role;
import io.github.tavim26.onlinestore.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    public int insert(Connection c, User user) throws SQLException {
        String sql = "INSERT INTO users (role, first_name, last_name, email, password_hash, cnp, address, phone) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getRole().name());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPasswordHash());
            ps.setString(6, user.getCnp());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getPhone());
            ps.executeUpdate();
            int id = JdbcUtils.generatedId(ps);
            user.setId(id);
            return id;
        }
    }

    public Optional<User> findById(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public Optional<User> findByEmail(Connection c, String email) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<User> findAll(Connection c) throws SQLException {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM users ORDER BY last_name, first_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(map(rs));
            }
        }
        return users;
    }

    /** Updates profile data. The balance is changed only through addToBalance / subtractFromBalance. */
    public boolean update(Connection c, User user) throws SQLException {
        String sql = "UPDATE users SET role = ?, first_name = ?, last_name = ?, email = ?, password_hash = ?, "
                + "cnp = ?, address = ?, phone = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getRole().name());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPasswordHash());
            ps.setString(6, user.getCnp());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getPhone());
            ps.setInt(9, user.getId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean addToBalance(Connection c, int userId, BigDecimal amount) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE users SET balance = balance + ? WHERE id = ?")) {
            ps.setBigDecimal(1, amount);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    /** Atomically subtracts from the balance. Returns false if the balance is insufficient. */
    public boolean subtractFromBalance(Connection c, int userId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE users SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setInt(2, userId);
            ps.setBigDecimal(3, amount);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    static User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                Role.valueOf(rs.getString("role")),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("cnp"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getBigDecimal("balance"),
                rs.getObject("created_at", LocalDateTime.class));
    }
}