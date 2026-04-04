package com.paynestsystem.common;

import java.util.Locale;

/**
 * Centralizes currency display so the application formats amounts consistently.
 */
public final class CurrencyFormatter {

    private CurrencyFormatter() {
    }

    public static String formatZar(double amount) {
        return "R" + String.format(Locale.ENGLISH, "%.0f", amount);
    }
}
