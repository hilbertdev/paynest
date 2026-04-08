package com.paynestsystem.types;

/**
 * Represents a monetary amount stored as <strong>cents</strong> (whole number)
 * paired with a {@link CurrencyCode}.
 *
 * <p>This is a <strong>Java record</strong> (introduced in Java 14). The compiler
 * automatically generates {@code equals}, {@code hashCode}, {@code toString},
 * and component accessor methods ({@code amountInCents()}, {@code currency()}).
 * Compare this with {@code Product.java}, which had to implement all of those
 * by hand.</p>
 *
 * <h3>Why cents instead of rands?</h3>
 * <p>Real fintech systems avoid {@code double} for money because floating-point
 * arithmetic can introduce rounding errors (e.g. {@code 0.1 + 0.2 != 0.3}).
 * Storing amounts as {@code long} cents eliminates that class of bugs entirely.
 * The existing {@code Product.price} uses {@code double} for simplicity, but
 * production code should prefer an approach like this one.</p>
 *
 * @param amountInCents the amount in the smallest currency unit (e.g. cents); must be >= 0
 * @param currency      the currency of this amount; must not be null
 */
public record Money(long amountInCents, CurrencyCode currency) {

    /**
     * Compact constructor — validates that the amount is non-negative
     * and the currency is provided.
     */
    public Money {
        if (amountInCents < 0) {
            throw new IllegalArgumentException("Amount in cents must be non-negative, got: " + amountInCents);
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
    }

    /**
     * Converts the cent-based amount to a decimal value in the major unit
     * (e.g. Rands, Dollars).
     *
     * @return the amount expressed in the major currency unit
     */
    public double toMajorUnit() {
        return amountInCents / 100.0;
    }

    /**
     * Adds another {@code Money} value to this one.
     * Both values must share the same currency.
     *
     * @param other the amount to add
     * @return a new {@code Money} representing the sum
     * @throws IllegalArgumentException if the currencies differ
     */
    public Money add(Money other) {
        if (this.currency != other.currency) {
            throw new IllegalArgumentException(
                    "Cannot add different currencies: " + this.currency + " and " + other.currency);
        }
        return new Money(this.amountInCents + other.amountInCents, this.currency);
    }

    /**
     * Returns a human-readable string like {@code "R125.50"}.
     *
     * @return the formatted amount with currency symbol
     */
    public String formatted() {
        return currency.getSymbol() + String.format(java.util.Locale.US, "%.2f", toMajorUnit());
    }
}
