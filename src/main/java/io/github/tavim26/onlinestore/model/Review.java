package io.github.tavim26.onlinestore.model;

import java.time.LocalDateTime;

public class Review
{
    private int reviewID;
    private User user;
    private Product product;
    private String reviewText;
    private int rating;
    private LocalDateTime reviewDate;


    public Review(int reviewID, User user, Product product, String reviewText, int rating, LocalDateTime reviewDate) {
        this.reviewID = reviewID;
        this.user = user;
        this.product = product;
        this.reviewText = reviewText;
        this.rating = rating;
        this.reviewDate = reviewDate;
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
}


