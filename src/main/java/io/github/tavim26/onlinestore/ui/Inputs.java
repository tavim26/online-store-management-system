package io.github.tavim26.onlinestore.ui;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Parses text typed into form fields. Each method returns null if the text is not valid. */
public final class Inputs {

    private static final DateTimeFormatter EXPIRY = DateTimeFormatter.ofPattern("MM/yy");

    private Inputs() {
    }

    /** "149.99" or "149,99" -> 149.99 */
    public static BigDecimal decimal(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer integer(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** "08/28" -> August 2028 */
    public static YearMonth expiry(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return YearMonth.parse(text.trim(), EXPIRY);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}