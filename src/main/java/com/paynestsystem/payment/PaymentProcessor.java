package com.paynestsystem.payment;

import com.paynestsystem.common.CurrencyFormatter;

import static com.paynestsystem.common.ValidationUtils.requireNonNegative;
import static com.paynestsystem.common.ValidationUtils.requireNonNull;

/**
 * Processes payments using any PaymentMethod implementation.
 * Accepts a payment method and amount, then prints the result.
 */
public class PaymentProcessor {

    /**
     * Processes a payment using the given payment method.
     *
     * @param method the payment method to use
     * @param amount the amount to charge
     */
    public boolean processPayment(PaymentMethod method, double amount) {
        requireNonNull(method, "method");
        requireNonNegative(amount, "amount");

        boolean success = method.processPayment(amount);
        if (success) {
            System.out.println("Payment successful via " + method.getPaymentType());
            System.out.println("Amount: " + CurrencyFormatter.formatZar(amount));
        } else {
            // Students can extend here: handle payment failure
            System.out.println("Payment failed via " + method.getPaymentType());
        }
        return success;
    }
}
