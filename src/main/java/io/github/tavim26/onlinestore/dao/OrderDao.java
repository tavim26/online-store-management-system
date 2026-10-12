package io.github.tavim26.onlinestore.dao;

import io.github.tavim26.onlinestore.model.Order;
import io.github.tavim26.onlinestore.model.OrderItem;
import io.github.tavim26.onlinestore.model.OrderStatus;
import io.github.tavim26.onlinestore.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderDao {

    private static final String SELECT_WITH_ITEMS =
            "SELECT o.id AS order_id, o.user_id, o.order_date, o.status, o.shipping_address, o.total, "
                    + "i.quantity, i.unit_price, "
                    + "p.id, p.name, p.description, p.price, p.stock "
                    + "FROM orders o "
                    + "JOIN order_items i ON i.order_id = o.id "
                    + "JOIN products p ON p.id = i.product_id ";

    /**
     * Inserts the order and all its items.
     * Must be called inside Database.inTransaction, so that either everything is saved or nothing is.
     */
    public int insert(Connection c, Order order) throws SQLException {
        String orderSql = "INSERT INTO orders (user_id, status, shipping_address, total) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setString(2, order.getStatus().name());
            ps.setString(3, order.getShippingAddress());
            ps.setBigDecimal(4, order.getTotal());
            ps.executeUpdate();
            order.setId(JdbcUtils.generatedId(ps));
        }

        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(itemSql)) {
            for (OrderItem item : order.getItems()) {
                ps.setInt(1, order.getId());
                ps.setInt(2, item.getProduct().getId());
                ps.setInt(3, item.getQuantity());
                ps.setBigDecimal(4, item.getUnitPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        return order.getId();
    }

    public Optional<Order> findById(Connection c, int id) throws SQLException {
        return query(c, "WHERE o.id = ?", id).stream().findFirst();
    }

    public List<Order> findByUser(Connection c, int userId) throws SQLException {
        return query(c, "WHERE o.user_id = ?", userId);
    }

    public List<Order> findAll(Connection c) throws SQLException {
        return query(c, "");
    }

    /**
     * Changes the status only if the order still has the expected status.
     * Returns false if someone else changed it in the meantime, so the same order
     * can never be cancelled (and refunded) twice.
     */
    public boolean updateStatus(Connection c, int orderId, OrderStatus expected, OrderStatus newStatus)
            throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ? AND status = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, orderId);
            ps.setString(3, expected.name());
            return ps.executeUpdate() == 1;
        }
    }

    /** Runs the JOIN query and groups the rows (one row per item) into orders. */
    private List<Order> query(Connection c, String where, Object... params) throws SQLException {
        String sql = SELECT_WITH_ITEMS + where + " ORDER BY o.order_date DESC, o.id DESC";
        Map<Integer, Order> orders = new LinkedHashMap<>();

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    Order order = orders.get(orderId);
                    if (order == null) {
                        order = new Order(
                                orderId,
                                rs.getInt("user_id"),
                                rs.getObject("order_date", LocalDateTime.class),
                                OrderStatus.valueOf(rs.getString("status")),
                                rs.getString("shipping_address"),
                                rs.getBigDecimal("total"),
                                new ArrayList<>());
                        orders.put(orderId, order);
                    }
                    Product product = ProductDao.map(rs);
                    order.getItems().add(new OrderItem(product, rs.getInt("quantity"), rs.getBigDecimal("unit_price")));
                }
            }
        }
        return new ArrayList<>(orders.values());
    }

    /** True if the user has a non-cancelled order containing the product. */
    public boolean hasPurchased(Connection c, int userId, int productId) throws SQLException {
        String sql = "SELECT 1 FROM orders o JOIN order_items i ON i.order_id = o.id "
                + "WHERE o.user_id = ? AND i.product_id = ? AND o.status <> 'CANCELLED' LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}