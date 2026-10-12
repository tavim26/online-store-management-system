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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}