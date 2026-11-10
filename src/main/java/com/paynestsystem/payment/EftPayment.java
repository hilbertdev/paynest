package com.paynestsystem.payment;

/**
 * Payment via Electronic Funds Transfer (EFT).
 * An EFT extends the shared PaymentMethod parent and joins the
 * BankSettlement group: it settles in 1-2 business days via the bank.
 */
public class EftPayment extends PaymentMethod implements BankSettlement {

    @Override
    protected boolean doProcess(double amount) {
        // Simulated: always succeeds. Students can extend here: add failure scenarios
        return true;
    }

    @Override
    public String getPaymentType() {
        return "EFT";
    }

    @Override
    public String getBankReference() {
        // Simulated: a fixed reference. Students can extend here: return a real bank reference
        return "EFT-PENDING";
    }
}
