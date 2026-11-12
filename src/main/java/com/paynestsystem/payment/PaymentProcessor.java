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
     * @return true if the rail accepted the payment, false if it declined.
     *         A decline is a normal business outcome (e.g. wallet balance too
     *         low), so it travels back as a return value, not an exception.
     */
    public boolean processPayment(PaymentMethod method, double amount) {
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
            // A rail applied its own rule and said no (e.g. wallet balance
            // too low). The processor just reports the answer - the rule
            // itself belongs to the rail, never to this class.
            System.out.println("Payment failed via " + method.getPaymentType());
        }
        return success;
    }
}
