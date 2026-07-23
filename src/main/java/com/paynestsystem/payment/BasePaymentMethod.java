package com.paynestsystem.payment;

/**
 * Shared base for simple payment methods that only differ by payment type.
 */
public abstract class BasePaymentMethod implements PaymentMethod {

    private final PaymentType paymentType;

    protected BasePaymentMethod(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public PaymentType getPaymentType() {
        return paymentType;
    }
}
