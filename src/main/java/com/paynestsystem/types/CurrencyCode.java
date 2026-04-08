package com.paynestsystem.types;

/**
 * Represents supported currencies on the PayNest platform.
 *
 * <p>This enum demonstrates that Java enums can carry <strong>fields</strong>,
 * <strong>constructors</strong>, and <strong>methods</strong> — they are not
 * limited to plain constants like {@code RiskLevel}.</p>
 *
 * <p>Each constant stores a display symbol, a human-readable name, and an
 * approximate exchange rate to South African Rands (ZAR).</p>
 */
public enum CurrencyCode {

    ZAR("R", "South African Rand", 1.0),
    USD("$", "US Dollar", 18.5),
    EUR("€", "Euro", 20.0),
    GBP("£", "British Pound", 23.0);

    private final String symbol;
    private final String displayName;
    private final double exchangeRateToZar;

    /**
     * Creates a currency constant.
     *
     * @param symbol            short symbol used for display (e.g. "R")
     * @param displayName       full name of the currency
     * @param exchangeRateToZar approximate rate: 1 unit of this currency = X ZAR
     */
    CurrencyCode(String symbol, String displayName, double exchangeRateToZar) {
        this.symbol = symbol;
        this.displayName = displayName;
        this.exchangeRateToZar = exchangeRateToZar;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getExchangeRateToZar() {
        return exchangeRateToZar;
    }

    /**
     * Converts the given amount from this currency to South African Rands.
     *
     * @param amount the amount in this currency
     * @return the equivalent amount in ZAR
     */
    public double convertToZar(double amount) {
        return amount * exchangeRateToZar;
    }
}
