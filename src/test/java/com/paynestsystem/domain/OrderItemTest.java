package com.paynestsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Capstone 1: line-subtotal arithmetic and quantity validation on OrderItem.
 */
class OrderItemTest {

    @Test
    void calculateTotal_returnsUnitPriceTimesQuantity() {
        Product mouse = new Product(2, "Mouse", 200);

        OrderItem item = new OrderItem(mouse, 2);

        assertEquals(400.0, item.calculateTotal(), 0.0001);
    }

    @Test
    void constructor_rejectsNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(null, 1));
    }

    @Test
    void constructor_rejectsZeroQuantity() {
        Product laptop = new Product(1, "Laptop", 12000);

        assertThrows(IllegalArgumentException.class, () -> new OrderItem(laptop, 0));
    }

    @Test
    void constructor_rejectsNegativeQuantity() {
        Product laptop = new Product(1, "Laptop", 12000);

        assertThrows(IllegalArgumentException.class, () -> new OrderItem(laptop, -1));
    }
}
