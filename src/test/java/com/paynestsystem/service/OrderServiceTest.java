package com.paynestsystem.service;

import com.paynestsystem.domain.Customer;
import com.paynestsystem.domain.Order;
import com.paynestsystem.domain.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Capstone 1: OrderService as a thin API over the domain.
 */
class OrderServiceTest {

    private final OrderService orderService = new OrderService();
    private final Customer customer = new Customer(1, "John Smith", "john@example.com");
    private final Product laptop = new Product(1, "Laptop", 12000);
    private final Product mouse = new Product(2, "Mouse", 200);

    @Test
    void createOrder_returnsEmptyOrderForCustomer() {
        Order order = orderService.createOrder(1, customer);

        assertEquals(1, order.getId());
        assertEquals(customer, order.getCustomer());
        assertEquals(0.0, orderService.calculateTotals(order), 0.0001);
    }

    @Test
    void addProductsToOrder_andCalculateTotals_matchDomain() {
        Order order = orderService.createOrder(1, customer);
        orderService.addProductsToOrder(order, laptop, 1);
        orderService.addProductsToOrder(order, mouse, 2);

        assertEquals(12400.0, orderService.calculateTotals(order), 0.0001);
        assertEquals(order.calculateTotal(), orderService.calculateTotals(order), 0.0001);
    }

    @Test
    void addProductsToOrder_rejectsInvalidQuantity() {
        Order order = orderService.createOrder(1, customer);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.addProductsToOrder(order, laptop, 0));
    }
}
