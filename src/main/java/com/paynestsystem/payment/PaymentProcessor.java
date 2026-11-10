package com.paynestsystem.payment;

/**
 * Accepts any PaymentMethod (polymorphism via the parent class) and
 * processes it. Also demonstrates the second kind of polymorphism:
 * asking a rail which capability groups it belongs to via interfaces.
 */
public class PaymentProcessor {

    /**
     * Processes a payment using the given payment method.
     *
     * @param method the payment method to use
     * @param amount the amount to charge
     */
    public void processPayment(PaymentMethod method, double amount) {
        // Settlement expectations come from the rail's capability group,
        // not from knowing which rail it is.
        if (method instanceof InstantSettlement) {
            InstantSettlement instant = (InstantSettlement) method;
            System.out.println("Settles: " + instant.settlementTime());
        } else if (method instanceof BankSettlement) {
            BankSettlement bank = (BankSettlement) method;
            System.out.println("Settles: " + bank.settlementTime()
                    + " (reference: " + bank.getBankReference() + ")");
        }

        boolean success = method.processPayment(amount);
        if (success) {
            System.out.println("Payment successful via " + method.getPaymentType());
            System.out.println("Amount: R" + String.format("%.0f", amount));
        } else {
            // Students can extend here: handle payment failure
            System.out.println("Payment failed via " + method.getPaymentType());
        }
    }
}
