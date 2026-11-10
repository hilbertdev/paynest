package com.paynestsystem.payment;

/**
 * Payment via credit or debit card.
 * A card payment extends the shared PaymentMethod parent and joins two
 * capability groups: it settles instantly and it can be refunded.
 */
public class CardPayment extends PaymentMethod implements InstantSettlement, Refundable {

    @Override
    protected boolean doProcess(double amount) {
        // Simulated: always succeeds. Students can extend here: add failure scenarios
        return true;
    }

    @Override
    public String getPaymentType() {
        return "CARD";
    }

    @Override
    public boolean refund(double amount) {
        // Simulated: always succeeds. Students can extend here: partial refunds, refund windows
        return true;
    }
}
