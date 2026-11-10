package com.paynestsystem.payment;

/**
 * Payment via digital wallet (e.g. PayNest Wallet).
 * A wallet payment extends the shared PaymentMethod parent and joins two
 * capability groups: it settles instantly and it can be refunded.
 */
public class WalletPayment extends PaymentMethod implements InstantSettlement, Refundable {

    @Override
    protected boolean doProcess(double amount) {
        // Simulated: always succeeds. Students can extend here: keep a balance and reject amounts above it
        return true;
    }

    @Override
    public String getPaymentType() {
        return "WALLET";
    }

    @Override
    public boolean refund(double amount) {
        // Simulated: always succeeds. Students can extend here: refund back into the wallet balance
        return true;
    }
}
