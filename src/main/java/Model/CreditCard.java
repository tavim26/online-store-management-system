package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreditCard {

    private int creditCardID;
    private User user;
    private String creditCardNumber;
    private String creditCardType;
    private String cvv;
    private double cardBalance;
    private LocalDate expirationDate;


    public CreditCard(int creditCardID, User user, String creditCardNumber, String creditCardType, String cvv, double cardBalance, LocalDate expirationDate) {
        this.creditCardID = creditCardID;
        this.user = user;
        this.creditCardNumber = creditCardNumber;
        this.creditCardType = creditCardType;
        this.cvv = cvv;
        this.cardBalance = cardBalance;
        this.expirationDate = expirationDate;
    }

    public int getCreditCardID() {
        return creditCardID;
    }

    public void setCreditCardID(int creditCardID) {
        this.creditCardID = creditCardID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public double getCardBalance() {
        return cardBalance;
    }

    public void setCardBalance(double cardBalance) {
        this.cardBalance = cardBalance;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
