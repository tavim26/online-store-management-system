package io.github.tavim26.onlinestore.ui;

import java.math.BigDecimal;
import java.util.Locale;

public final class Formats {

    private Formats() {
    }

    /** E.g. 1299 -> "1,299.00 RON" */
    public static String money(BigDecimal amount) {
        return String.format(Locale.US, "%,.2f RON", amount);
    }
}