package io.github.tavim26.onlinestore.service;

import io.github.tavim26.onlinestore.config.Database;
import io.github.tavim26.onlinestore.dao.PaymentCardDao;
import io.github.tavim26.onlinestore.dao.UserDao;
import io.github.tavim26.onlinestore.model.PaymentCard;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

/** Simulated payments: a saved card is used to top up the user's store balance. */
public class PaymentCardService {

    private static final BigDecimal MAX_TOP_UP = new BigDecimal("10000.00");

    private final PaymentCardDao cardDao = new PaymentCardDao();
    private final UserDao userDao = new UserDao();

    public PaymentCard addCard(int userId, String cardNumber, YearMonth expiry) {
        String digits = cardNumber == null ? "" : cardNumber.replaceAll("[\\s-]", "");
        YearMonth now = YearMonth.now();
        new Validator()
                .check(digits.matches("\\d{16}") && passesLuhn(digits), "Card number is not valid.")
                .check(expiry != null && !expiry.isBefore(now), "The card is expired.")
                .check(expiry == null || !expiry.isAfter(now.plusYears(10)), "Expiry date is too far in the future.")
                .throwIfInvalid();

        // Only the last four digits are stored; the full number never reaches the database.
        PaymentCard card = new PaymentCard(userId, detectType(digits), digits.substring(12), expiry.atEndOfMonth());
        Database.withConnection(c -> cardDao.insert(c, card));
        return card;
    }

    public List<PaymentCard> getCards(int userId) {
        return Database.withConnection(c -> cardDao.findByUser(c, userId));
    }

    public void removeCard(int userId, int cardId) {
        Database.inTransaction(c -> {
            findOwnedCard(c, userId, cardId);
            return cardDao.delete(c, cardId);
        });
    }

    public void topUp(int userId, int cardId, BigDecimal amount) {
        new Validator()
                .check(amount != null && amount.signum() > 0 && amount.scale() <= 2
                                && amount.compareTo(MAX_TOP_UP) <= 0,
                        "Amount must be between 0.01 and 10,000.00.")
                .throwIfInvalid();

        Database.inTransaction(c -> {
            PaymentCard card = findOwnedCard(c, userId, cardId);
            if (card.isExpired()) {
                throw new BusinessException("The card is expired.");
            }
            return userDao.addToBalance(c, userId, amount);
        });
    }

    private PaymentCard findOwnedCard(Connection c, int userId, int cardId) throws SQLException {
        return cardDao.findByUser(c, userId).stream()
                .filter(card -> card.getId() == cardId)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Card not found."));
    }

    /** Luhn checksum, used by all major card networks to catch typing mistakes. */
    static boolean passesLuhn(String digits) {
        int sum = 0;
        boolean doubleIt = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleIt) {
                d *= 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        return sum % 10 == 0;
    }

    static String detectType(String digits) {
        if (digits.startsWith("4")) {
            return "VISA";
        }
        if (digits.matches("^(5[1-5]|2[2-7]).*")) {
            return "MASTERCARD";
        }
        return "CARD";
    }
}