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

    // Alt+Insert -> Getter and Setter (all fields)
}