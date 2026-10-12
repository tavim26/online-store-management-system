package io.github.tavim26.onlinestore.model;

public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    /** True while the order has not been shipped yet. */
    public boolean isCancellable() {
        return this == PENDING || this == PAID;
    }

    /** The status an admin can move the order to next, or null if there is none. */
    public OrderStatus next() {
        return switch (this) {
            case PAID -> SHIPPED;
            case SHIPPED -> DELIVERED;
            default -> null;
        };
    }
}