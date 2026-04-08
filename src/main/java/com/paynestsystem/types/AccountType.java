package com.paynestsystem.types;

/**
 * A <strong>sealed interface</strong> representing a bank account type.
 *
 * <p>Sealed types (Java 17) restrict which classes may implement or extend them.
 * Only the classes listed in the {@code permits} clause are allowed.
 * This gives the compiler full knowledge of all possible subtypes, enabling
 * <strong>exhaustive pattern matching</strong> in {@code switch} expressions
 * (no {@code default} branch needed).</p>
 *
 * <p>PayNest supports three account types: savings, cheque, and credit.
 * Each has different rules for balances, limits, and interest.</p>
 *
 * @see SavingsAccount
 * @see ChequeAccount
 * @see CreditAccount
 */
public sealed interface AccountType permits SavingsAccount, ChequeAccount, CreditAccount {

    /**
     * @return the name of the account holder
     */
    String getAccountHolder();

    /**
     * @return the current balance of the account
     */
    Money getBalance();

    /**
     * @return a human-readable description of this account
     */
    String describe();
}
