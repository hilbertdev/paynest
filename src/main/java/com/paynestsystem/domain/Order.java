package com.paynestsystem.domain;

import com.paynestsystem.payment.PaymentMethod;
import com.paynestsystem.payment.PaymentProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An order placed by a customer: identity, owned line items, and totals.
 * <p>
 * The order <em>owns</em> its {@link OrderItem} list. Callers add lines only
 * through {@link #addItem(Product, int)}; they must not mutate the backing
 * collection directly (see {@link #getItems()}). That keeps grand totals
 * trustworthy — one code path computes what the customer owes.
 */
public class Order {

    private final int id;
    private final Customer customer;
    /** Mutable privately; exposed to callers only as an unmodifiable view. */
    private final List<OrderItem> items;

    /**
     * Creates an empty order for the given customer.
     *
     * @param id       unique identifier for the order
     * @param customer the customer placing the order
     */
    public Order(int id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    /**
     * Adds a product line. Validation (null product, quantity &gt; 0) is enforced
     * by {@link OrderItem}'s constructor so invalid lines never enter the list.
     *
     * @param product  the product to add
     * @param quantity the number of units (must be &gt; 0)
     */
    public void addItem(Product product, int quantity) {
        // Domain owns validation — OrderItem rejects bad input loudly.
        OrderItem orderItem = new OrderItem(product, quantity);
        items.add(orderItem);
    }

    /**
     * Grand total: sum of every line's {@link OrderItem#calculateTotal()}.
     * Same definition used by {@link #printSummary()} so console output can be
     * reconciled manually with no hidden magic numbers.
     *
     * @return the total amount in Rand
     */
    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total = total + item.calculateTotal();
        }
        return total;
    }

    /**
     * Human-readable receipt: customer, each line (name, qty, line subtotal),
     * then grand total from {@link #calculateTotal()} — not a separate figure.
     */
    public void printSummary() {
        System.out.println("Order Summary");
        System.out.println("Customer: " + customer.getName());
        System.out.println();
        System.out.println("Items:");
        for (OrderItem item : items) {
            String line = item.getProduct().getName() + " x" + item.getQuantity()
                    + " - R" + String.format("%.0f", item.calculateTotal());
            System.out.println(line);
        }
        System.out.println();
        // Grand total must match calculateTotal() — single source of truth.
        System.out.println("Total: R" + String.format("%.0f", calculateTotal()));
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    /**
     * Read-only view of line items. Returning the live {@link ArrayList} would
     * let callers {@code clear()} or {@code add()} outside {@link #addItem},
     * silently breaking totals — so we wrap with {@link Collections#unmodifiableList}.
     *
     * @return unmodifiable list of order items
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Capstone 2: complete the order by processing payment.
     * Capstone 1 is read-only checkout (summary only); this method is used
     * by the Capstone 2 demo in {@code PayNestApplication}.
     *
     * @param paymentMethod the payment method to use for checkout
     */
    public void checkout(PaymentMethod paymentMethod) {
        double total = calculateTotal();
        PaymentProcessor processor = new PaymentProcessor();
        processor.processPayment(paymentMethod, total);
        System.out.println("Order completed successfully.");
    }
}
