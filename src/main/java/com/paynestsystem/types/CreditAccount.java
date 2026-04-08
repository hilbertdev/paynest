package com.paynestsystem.types;

/**
 * A credit account with a spending limit.
 *
 * <p>This is a <strong>final</strong> permitted subtype of the sealed
 * {@link AccountType} interface.</p>
 */
public final class CreditAccount implements AccountType {

    private final String accountHolder;
    private final Money balance;
    private final Money creditLimit;

    /**
     * Creates a new credit account.
     *
     * @param accountHolder name of the account holder
     * @param balance       current amount owed
     * @param creditLimit   maximum credit allowed
     */
    public CreditAccount(String accountHolder, Money balance, Money creditLimit) {
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.creditLimit = creditLimit;
    }

    @Override
    public String getAccountHolder() {
        return accountHolder;
    }

    @Override
    public Money getBalance() {
        return balance;
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    /**
     * Calculates the remaining credit available (limit minus current balance owed).
     *
     * @return a {@link Money} representing available credit
     */
    public Money availableCredit() {
        long availableCents = creditLimit.amountInCents() - balance.amountInCents();
        return new Money(availableCents, balance.currency());
    }

    @Override
    public String describe() {
        return "Credit account for " + accountHolder
                + " (" + balance.formatted()
                + " / " + creditLimit.formatted() + " limit)";
    }
}
