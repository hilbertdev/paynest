package com.paynestsystem.types;

/**
 * A cheque (current) account with an overdraft facility.
 *
 * <p>This is a <strong>final</strong> permitted subtype of the sealed
 * {@link AccountType} interface.</p>
 */
public final class ChequeAccount implements AccountType {

    private final String accountHolder;
    private final Money balance;
    private final Money overdraftLimit;

    /**
     * Creates a new cheque account.
     *
     * @param accountHolder  name of the account holder
     * @param balance        current balance
     * @param overdraftLimit maximum overdraft allowed
     */
    public ChequeAccount(String accountHolder, Money balance, Money overdraftLimit) {
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public String getAccountHolder() {
        return accountHolder;
    }

    @Override
    public Money getBalance() {
        return balance;
    }

    public Money getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Calculates the total funds available (balance + overdraft).
     *
     * @return a {@link Money} representing the available funds
     */
    public Money availableFunds() {
        return balance.add(overdraftLimit);
    }

    @Override
    public String describe() {
        return "Cheque account for " + accountHolder
                + " (" + balance.formatted()
                + " with " + overdraftLimit.formatted() + " overdraft)";
    }
}
