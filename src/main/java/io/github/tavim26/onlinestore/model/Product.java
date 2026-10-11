package io.github.tavim26.onlinestore.model;

import java.math.BigDecimal;

public class Product {

    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;

    /** A new product, not yet saved in the database. */
    public Product(String name, String description, BigDecimal price, int stock) {
        this(0, name, description, price, stock);
    }

    /** A product loaded from the database. */
    public Product(int id, String name, String description, BigDecimal price, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}