package com.paynestsystem.types;

/**
 * A savings account that earns interest on its balance.
 *
 * <p>This is a <strong>final</strong> class that implements the sealed
 * {@link AccountType} interface. Being {@code final} means no further
 * subclassing is allowed — the type hierarchy is completely closed.</p>
 */
public final class SavingsAccount implements AccountType {

    private final String accountHolder;
    private final Money balance;
    private final double interestRate;

    /**
     * Creates a new savings account.
     *
     * @param accountHolder name of the account holder
     * @param balance       current balance
     * @param interestRate  annual interest rate as a decimal (e.g. 0.055 for 5.5%)
     */
    public SavingsAccount(String accountHolder, Money balance, double interestRate) {
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.interestRate = interestRate;
    }

    @Override
    public String getAccountHolder() {
        return accountHolder;
    }

    @Override
    public Money getBalance() {
        return balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Calculates the annual interest earned on the current balance.
     *
     * @return a {@link Money} representing one year's interest
     */
    public Money calculateAnnualInterest() {
        long interestCents = Math.round(balance.amountInCents() * interestRate);
        return new Money(interestCents, balance.currency());
    }

    @Override
    public String describe() {
        return "Savings account for " + accountHolder
                + " (" + balance.formatted()
                + " @ " + String.format(java.util.Locale.US, "%.1f", interestRate * 100) + "% interest)";
    }
}
