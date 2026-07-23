package com.paynestsystem.payment;

/**
 * Payment via credit or debit card.
 * Simulates a successful card payment (no real processing).
 */
public class CardPayment extends BasePaymentMethod {

    public CardPayment() {
        super(PaymentType.CARD);
    }

    @Override
    public boolean processPayment(double amount) {
        // Simulated: always succeeds. Students can extend here: add failure scenarios
        return true;
    }
}
