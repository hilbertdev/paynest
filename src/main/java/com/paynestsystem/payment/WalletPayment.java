package com.paynestsystem.payment;

/**
 * Payment via digital wallet (e.g. PayNest Wallet).
 *
 * A wallet payment extends the shared PaymentMethod parent and joins two
 * capability groups: it settles instantly and it can be refunded.
 *
 * This rail is the worked example of a PER-RAIL BUSINESS RULE - the part of
 * the brief worth the most marks ("correct per-rail behaviour"):
 *
 *   A wallet can only spend the balance it actually holds.
 *
 * Notice WHERE the rule lives: on the rail, not in the checkout. The checkout
 * (Order.checkout -> PaymentProcessor) does not know wallets have balances,
 * and it should not have to. Each rail owns its own rules:
 *   - a wallet checks a balance,
 *   - a BuyNowPayLater rail would check a spend limit,
 *   - a card rail might check a daily cap.
 * What every rail shares (rejecting amounts of R0 or less) stays written once
 * in the abstract parent - see PaymentMethod.processPayment. Shared rules go
 * up to the parent; rules only one rail understands stay on the rail.
 */
public class WalletPayment extends PaymentMethod implements InstantSettlement, Refundable {

    /**
     * The money this wallet holds right now.
     * This is the rail's own state: no other rail has a balance, so no other
     * class should manage it. Private, changed only by paying and refunding.
     */
    private double balance;

    /**
     * A wallet that starts with an opening balance.
     *
     * @param openingBalance how much money the wallet holds at the start
     */
    public WalletPayment(double openingBalance) {
        this.balance = openingBalance;
    }

    /**
     * A demo wallet with R1,000 in it, so examples and demos stay one line.
     * Real checkouts should prefer the constructor that says the balance out
     * loud: new WalletPayment(250.00) reads like the rule it tests.
     */
    public WalletPayment() {
        this(1000.0);
    }

    /**
     * The rail's own processing, called ONLY after the parent's shared
     * validation (amount greater than R0) has already passed.
     *
     * The wallet's own rule: decline the payment when the amount is more than
     * the balance. A decline is a normal answer, not an error - we return
     * false and let the checkout decide what to do. We do not throw.
     */
    @Override
    protected boolean doProcess(double amount) {
        if (amount > balance) {
            // The one `if` that makes this a real rail: the rule of the wallet.
            System.out.println(
                    "Wallet declined: R" + String.format("%.2f", amount)
                            + " is more than the balance of R" + String.format("%.2f", balance)
            );
            return false;
        }

        // Payment succeeds: the balance goes down by exactly what was charged.
        balance -= amount;
        System.out.println(
                "Wallet paid R" + String.format("%.2f", amount)
                        + " (remaining balance: R" + String.format("%.2f", balance) + ")"
        );
        return true;
    }

    /**
     * The current balance. Tests and demos read this to prove the rule ran:
     * a declined payment leaves the balance untouched; a successful one
     * reduces it by exactly the amount charged.
     *
     * @return the money left in the wallet
     */
    public double getBalance() {
        return balance;
    }

    @Override
    public String getPaymentType() {
        return "WALLET";
    }

    /**
     * Refund money back into the wallet. This is the Refundable capability
     * doing real work now: the refunded amount is added back to the balance,
     * so the rail's state stays honest across pay and refund.
     */
    @Override
    public boolean refund(double amount) {
        if (amount <= 0) {
            // Same shared idea as the parent's amount check, applied to refunds.
            System.out.println("Refund rejected: amount must be greater than R0");
            return false;
        }
        balance += amount;
        System.out.println(
                "Wallet refunded R" + String.format("%.2f", amount)
                        + " (new balance: R" + String.format("%.2f", balance) + ")"
        );
        return true;
    }
}
