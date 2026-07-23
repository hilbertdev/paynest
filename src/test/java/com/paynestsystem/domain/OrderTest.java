package com.paynestsystem.domain;

import com.paynestsystem.payment.PaymentProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void buildSummary_formatsOrderDetailsConsistently() {
        Order order = new Order(1, new Customer(1, "John Smith", "john@example.com"), new PaymentProcessor());
        order.addItem(new Product(1, "Laptop", 12000), 1);
        order.addItem(new Product(2, "Mouse", 200), 2);

        String expected = String.join(System.lineSeparator(),
                List.of(
                        "Order Summary",
                        "Customer: John Smith",
                        "",
                        "Items:",
                        "Laptop x1 - R12000",
                        "Mouse x2 - R400",
                        "",
                        "Total: R12400"));

        assertEquals(expected, order.buildSummary());
    }

    @Test
    void getItems_returnsReadOnlyView() {
        Order order = new Order(1, new Customer(1, "John Smith", "john@example.com"));
        order.addItem(new Product(1, "Laptop", 12000), 1);

        assertThrows(UnsupportedOperationException.class, () -> order.getItems().add(new OrderItem(
                new Product(2, "Mouse", 200),
                1)));
    }

    @Test
    void addItem_rejectsInvalidQuantity() {
        Order order = new Order(1, new Customer(1, "John Smith", "john@example.com"));

        assertThrows(IllegalArgumentException.class, () -> order.addItem(new Product(1, "Laptop", 12000), 0));
    }
}
