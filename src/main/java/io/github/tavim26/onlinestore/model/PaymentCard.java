package io.github.tavim26.onlinestore.model;

import java.time.LocalDate;

public class PaymentCard {

    private int id;
    private int userId;
    private String cardType;
    private String lastFour;
    private LocalDate expiryDate;

    /** A new card, not yet saved in the database. */
    public PaymentCard(int userId, String cardType, String lastFour, LocalDate expiryDate) {
        this(0, userId, cardType, lastFour, expiryDate);
    }

    /** A card loaded from the database. */
    public PaymentCard(int id, int userId, String cardType, String lastFour, LocalDate expiryDate) {
        this.id = id;
        this.userId = userId;
        this.cardType = cardType;
        this.lastFour = lastFour;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    /** E.g. "VISA **** 4242" */
    public String getMaskedNumber() {
        return cardType + " **** " + lastFour;
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getLastFour() {
        return lastFour;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}