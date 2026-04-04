package com.paynestsystem.payment;

/**
 * Payment via digital wallet (e.g. PayNest Wallet).
 * Simulates a successful wallet payment (no real processing).
 */
public class WalletPayment extends BasePaymentMethod {

    public WalletPayment() {
        super(PaymentType.WALLET);
    }

    @Override
    public boolean processPayment(double amount) {
        // Simulated: always succeeds. Students can extend here: add failure scenarios
        return true;
    }
}
