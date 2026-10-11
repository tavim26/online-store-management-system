package io.github.tavim26.onlinestore.model;

import java.math.BigDecimal;

public class OrderItem {

    private Product product;
    private int quantity;
    private BigDecimal unitPrice;

    /** A new item: the price is taken from the product at this moment. */
    public OrderItem(Product product, int quantity) {
        this(product, quantity, product.getPrice());
    }

    /** An item loaded from the database, with the price paid at the time. */
    public OrderItem(Product product, int quantity, BigDecimal unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Alt+Insert -> Getter and Setter (all fields)
}