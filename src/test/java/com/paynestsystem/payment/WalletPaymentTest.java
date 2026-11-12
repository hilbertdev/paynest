package com.paynestsystem.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.paynestsystem.domain.Customer;
import com.paynestsystem.domain.Email;
import com.paynestsystem.domain.Order;
import com.paynestsystem.domain.Product;

/**
 * Tests for the wallet's OWN business rule: a wallet can only spend the
 * balance it holds.
 *
 * This is the test class for the "per-rail behaviour" idea: every rail
 * applies its own rules to the amount, and the checkout simply trusts the
 * rail's answer. Read the tests top to bottom - each one names a behaviour.
 */
class WalletPaymentTest {

    /**
     * The rule itself: an amount above the balance is declined,
     * and a declined payment changes nothing.
     */
    @Test
    void walletDeclinesAmountAboveBalance() {
        WalletPayment wallet = new WalletPayment(100.0);

        boolean paid = wallet.processPayment(150.0);

        assertFalse(paid, "A wallet cannot spend more than it holds");
        assertEquals(100.0, wallet.getBalance(), 0.001,
                "A declined payment must leave the balance untouched");
    }

    /**
     * The happy path: within the balance, payment succeeds and the balance
     * drops by exactly the amount charged.
     */
    @Test
    void walletPaysWithinBalanceAndDeductsIt() {
        WalletPayment wallet = new WalletPayment(100.0);

        boolean paid = wallet.processPayment(60.0);

        assertTrue(paid);
        assertEquals(40.0, wallet.getBalance(), 0.001,
                "A successful payment reduces the balance by the amount charged");
    }

    /**
     * The SHARED rule still comes from the abstract parent: an amount of R0
     * or less is rejected before the wallet's own logic even runs. Written
     * once in PaymentMethod, inherited by every rail - not repeated here.
     */
    @Test
    void sharedAmountValidationStillApplies() {
        WalletPayment wallet = new WalletPayment(100.0);

        assertFalse(wallet.processPayment(0), "Zero is rejected by the parent, not the rail");
        assertFalse(wallet.processPayment(-50), "Negative amounts are rejected by the parent");
        assertEquals(100.0, wallet.getBalance(), 0.001);
    }

    /**
     * The Refundable capability does real work: a refund puts the money back.
     */
    @Test
    void refundTopsUpTheBalance() {
        WalletPayment wallet = new WalletPayment(100.0);
        wallet.processPayment(60.0);

        boolean refunded = wallet.refund(60.0);

        assertTrue(refunded);
        assertEquals(100.0, wallet.getBalance(), 0.001,
                "A refund returns the money to the wallet");
    }

    /**
     * The failure path through the real checkout: when the wallet declines,
     * checkout returns false and the order is not completed. Test the failure
     * path, not only the happy path.
     */
    @Test
    void checkoutReturnsFalseWhenWalletDeclines() {
        Order order = new Order(1, new Customer(1, "Test Customer", new Email("test@example.com")));
        order.addItem(new Product(1, "Laptop", 12000.0), 1);

        WalletPayment wallet = new WalletPayment(500.0);

        assertFalse(order.checkout(wallet),
                "Checkout must say no when the rail says no");
        assertEquals(500.0, wallet.getBalance(), 0.001);
    }

    /**
     * And the same checkout succeeds untouched when the rail's rule is met -
     * the checkout code does not change per rail, only the rail's answer does.
     */
    @Test
    void checkoutReturnsTrueWhenWalletCanAffordIt() {
        Order order = new Order(1, new Customer(1, "Test Customer", new Email("test@example.com")));
        order.addItem(new Product(1, "Mouse", 200.0), 1);

        WalletPayment wallet = new WalletPayment(500.0);

        assertTrue(order.checkout(wallet));
        assertEquals(300.0, wallet.getBalance(), 0.001);
    }
}
