package io.github.tavim26.onlinestore.service;

import io.github.tavim26.onlinestore.config.Database;
import io.github.tavim26.onlinestore.dao.OrderDao;
import io.github.tavim26.onlinestore.dao.ProductDao;
import io.github.tavim26.onlinestore.dao.UserDao;
import io.github.tavim26.onlinestore.model.Order;
import io.github.tavim26.onlinestore.model.OrderItem;
import io.github.tavim26.onlinestore.model.OrderStatus;
import io.github.tavim26.onlinestore.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {

    private final OrderDao orderDao = new OrderDao();
    private final ProductDao productDao = new ProductDao();
    private final UserDao userDao = new UserDao();

    /**
     * Places an order paid from the user's balance.
     *
     * @param cart product id -> quantity
     */
    public Order placeOrder(int userId, Map<Integer, Integer> cart, String shippingAddress) {
        new Validator()
                .check(cart != null && !cart.isEmpty(), "The cart is empty.")
                .check(cart == null || cart.values().stream().allMatch(q -> q != null && q > 0),
                        "All quantities must be positive.")
                .check(!Validator.isBlank(shippingAddress) && shippingAddress.length() <= 255,
                        "Shipping address is required (max 255 characters).")
                .throwIfInvalid();

        return Database.inTransaction(c -> {
            // Prices are always read from the database, never trusted from the cart.
            List<OrderItem> items = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                Product product = productDao.findById(c, entry.getKey())
                        .orElseThrow(() -> new BusinessException("A product in your cart no longer exists."));
                items.add(new OrderItem(product, entry.getValue()));
            }

            Order order = new Order(userId, shippingAddress.trim(), items);
            order.setStatus(OrderStatus.PAID);
            orderDao.insert(c, order);

            for (OrderItem item : items) {
                if (!productDao.decreaseStock(c, item.getProduct().getId(), item.getQuantity())) {
                    throw new BusinessException("Not enough stock for " + item.getProduct().getName() + ".");
                }
            }
            if (!userDao.subtractFromBalance(c, userId, order.getTotal())) {
                throw new BusinessException("Insufficient balance. Order total: " + order.getTotal() + ".");
            }
            return orderDao.findById(c, order.getId()).orElseThrow();
        });
    }

    /** Cancels a not-yet-shipped order: restores the stock and refunds the user. */
    public void cancelOrder(int userId, int orderId) {
        Database.inTransaction(c -> {
            Order order = orderDao.findById(c, orderId)
                    .filter(o -> o.getUserId() == userId)
                    .orElseThrow(() -> new BusinessException("Order not found."));

            OrderStatus current = order.getStatus();
            if (current != OrderStatus.PENDING && current != OrderStatus.PAID) {
                throw new BusinessException("Only orders that have not been shipped can be cancelled.");
            }
            if (!orderDao.updateStatus(c, orderId, current, OrderStatus.CANCELLED)) {
                throw new BusinessException("The order was modified in the meantime. Please try again.");
            }

            for (OrderItem item : order.getItems()) {
                productDao.increaseStock(c, item.getProduct().getId(), item.getQuantity());
            }
            if (current == OrderStatus.PAID) {
                userDao.addToBalance(c, userId, order.getTotal());
            }
            return null;
        });
    }

    /** Admin action: PAID -> SHIPPED -> DELIVERED. Returns the new status. */
    public OrderStatus advanceStatus(int orderId) {
        return Database.inTransaction(c -> {
            Order order = orderDao.findById(c, orderId)
                    .orElseThrow(() -> new BusinessException("Order not found."));

            OrderStatus next = switch (order.getStatus()) {
                case PAID -> OrderStatus.SHIPPED;
                case SHIPPED -> OrderStatus.DELIVERED;
                default -> throw new BusinessException(
                        "An order with status " + order.getStatus() + " cannot be advanced.");
            };
            if (!orderDao.updateStatus(c, orderId, order.getStatus(), next)) {
                throw new BusinessException("The order was modified in the meantime. Please try again.");
            }
            return next;
        });
    }

    public List<Order> getOrdersForUser(int userId) {
        return Database.withConnection(c -> orderDao.findByUser(c, userId));
    }

    public List<Order> getAllOrders() {
        return Database.withConnection(orderDao::findAll);
    }
}