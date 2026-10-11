package io.github.tavim26.onlinestore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private int id;
    private int userId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private String shippingAddress;
    private BigDecimal total;
    private List<OrderItem> items;

    /** A new order, not yet saved in the database. */
    public Order(int userId, String shippingAddress, List<OrderItem> items) {
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PENDING;
        this.items = new ArrayList<>(items);
        this.total = calculateTotal();
    }

    /** An order loaded from the database. */
    public Order(int id, int userId, LocalDateTime orderDate, OrderStatus status,
                 String shippingAddress, BigDecimal total, List<OrderItem> items) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.total = total;
        this.items = new ArrayList<>(items);
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}