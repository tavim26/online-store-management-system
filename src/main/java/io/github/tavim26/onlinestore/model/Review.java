package io.github.tavim26.onlinestore.model;

import java.time.LocalDateTime;

public class Review {

    private int id;
    private int userId;
    private int productId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    /** A new review, not yet saved in the database. */
    public Review(int userId, int productId, int rating, String comment) {
        this(0, userId, productId, rating, comment, null);
    }

    /** A review loaded from the database. */
    public Review(int id, int userId, int productId, int rating, String comment,
                  LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}