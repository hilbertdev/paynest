package com.paynestsystem.payment;

/**
 * Payment via Electronic Funds Transfer (EFT).
 * Simulates a successful EFT payment (no real processing).
 */
public class EftPayment extends BasePaymentMethod {

    public EftPayment() {
        super(PaymentType.EFT);
    }

    @Override
    public boolean processPayment(double amount) {
        // Simulated: always succeeds. Students can extend here: add failure scenarios
        return true;
    }
}
