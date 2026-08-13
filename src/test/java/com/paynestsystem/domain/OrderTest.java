package com.paynestsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Capstone 1: order totals, edge cases, validation, and encapsulation.
 */
class OrderTest {

    private final Customer customer = new Customer(1, "John Smith", new Email("john@example.com"));
    private final Product laptop = new Product(1, "Laptop", 12000);
    private final Product mouse = new Product(2, "Mouse", 200);

    @Test
    void calculateTotal_emptyOrder_isZero() {
        Order order = new Order(1, customer);

        assertEquals(0.0, order.calculateTotal(), 0.0001);
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void calculateTotal_singleLine() {
        Order order = new Order(1, customer);
        order.addItem(laptop, 1);

        assertEquals(12000.0, order.calculateTotal(), 0.0001);
    }

    @Test
    void calculateTotal_multipleLinesWithQuantityGreaterThanOne() {
        Order order = new Order(1, customer);
        order.addItem(laptop, 1);
        order.addItem(mouse, 2);

        // Laptop 12000 + Mouse 400 = 12400 — same figures as the demo receipt.
        assertEquals(12400.0, order.calculateTotal(), 0.0001);
        assertEquals(2, order.getItems().size());
    }

    @Test
    void calculateTotal_equalsSumOfLineSubtotals() {
        Order order = new Order(1, customer);
        order.addItem(laptop, 1);
        order.addItem(mouse, 2);

        double sumOfLines = 0.0;
        for (OrderItem item : order.getItems()) {
            sumOfLines += item.calculateTotal();
        }

        assertEquals(sumOfLines, order.calculateTotal(), 0.0001);
    }

    @Test
    void addItem_rejectsInvalidQuantity() {
        Order order = new Order(1, customer);

        assertThrows(IllegalArgumentException.class, () -> order.addItem(laptop, 0));
        assertThrows(IllegalArgumentException.class, () -> order.addItem(mouse, -5));
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void addItem_rejectsNullProduct() {
        Order order = new Order(1, customer);

        assertThrows(IllegalArgumentException.class, () -> order.addItem(null, 1));
    }

    @Test
    void getItems_returnsUnmodifiableList() {
        Order order = new Order(1, customer);
        order.addItem(laptop, 1);

        assertThrows(UnsupportedOperationException.class, () -> order.getItems().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> order.getItems().add(new OrderItem(mouse, 1)));
        assertEquals(1, order.getItems().size());
        assertEquals(12000.0, order.calculateTotal(), 0.0001);
    }
}
