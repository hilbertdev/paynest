# PayNest

A simplified fintech/ecommerce platform for teaching Java. This project simulates a commerce backend where merchants can create products, accept customer orders, and process payments.

## Project Overview

PayNest is a fictional platform that allows merchants to:

- Create products with names and prices
- Accept customer orders
- Process payments via multiple payment methods (card, EFT, wallet)

The codebase is designed for beginner–intermediate Java students and will be extended through multiple capstones. It uses pure Java (no frameworks) with a clear, readable structure.

## Company Background

PayNest is a fictional fintech company providing a simplified commerce backend. The platform enables merchants to manage products, handle customer orders, and accept various payment types. This project represents a teaching simulation of such a system.

## How to Run the Project

### Prerequisites

- Java 21
- Maven 3.6+

### Build and Run

```bash
# Compile the project
mvn compile

# Run the application
mvn exec:java
```

Alternatively:

```bash
mvn compile exec:java -Dexec.mainClass="com.paynestsystem.app.PayNestApplication"
```

### Expected Output

```
Order Summary
Customer: John Smith

Items:
Laptop x1 - R12000
Mouse x2 - R400

Total: R12400

Payment successful via CARD
Amount: R12400
Order completed successfully.
```

## Capstone 1 Description

**Core Commerce Engine**

Capstone 1 introduces the fundamental domain models and business logic:

- **Domain Models:** `Product`, `Customer`, `OrderItem`, `Order`
- **Business Service:** `OrderService` for creating orders and adding products
- **CLI Application:** `PayNestApplication` demonstrates the full flow: create products, create a customer, create an order, add products, and print a summary

Students learn: classes, objects, constructors, encapsulation, collections, and basic business logic.

## Capstone 2 Description

**OOP Payment System**

Capstone 2 extends the codebase with a payment system using interfaces and polymorphism:

- **Payment Interface:** `PaymentMethod` with `processPayment(double amount)` and `getPaymentType()`
- **Implementations:** `CardPayment`, `EftPayment`, `WalletPayment`
- **Payment Processor:** `PaymentProcessor` accepts any `PaymentMethod` and processes the payment
- **Order Update:** `Order.checkout(PaymentMethod)` calculates the total, processes payment, and prints confirmation

Students learn: interfaces, inheritance, polymorphism, and dependency design.

## Capstone 3 Description

**Payment Orchestration**

Capstone 3 introduces an orchestration layer that intelligently routes payments to the best provider based on multiple criteria:

- **Region:** `Region` enum represents geographic regions (ZA, EU, US, UK, APAC) where customers pay from
- **Payment Provider:** `PaymentProvider` models a provider with availability, latency, supported regions, fee structure, and amount thresholds
- **Fee Calculator:** `FeeCalculator` computes the fee and total charge for a given provider and amount
- **Payment Orchestrator:** `PaymentOrchestrator` selects the best provider by filtering on payment type, region, availability, and amount range, then choosing the lowest-latency candidate
- **Customer Update:** `Customer` now includes a `Region` field for orchestration-aware routing

### Orchestration Routing Rules

1. **Payment type:** Provider must support the requested payment type (CARD, EFT, WALLET)
2. **Region:** Provider must operate in the customer's region
3. **Availability:** Only available providers are considered
4. **Amount thresholds:** Payment amount must fall within the provider's accepted range (high-value payments route to premium providers)
5. **Latency:** Among eligible providers, the one with the lowest latency is selected

Students learn: orchestration patterns, multi-criteria decision making, fee calculation, and routing logic.

## Learning Objectives

- **Capstone 1:** Classes, objects, constructors, encapsulation, collections (`List`), basic business logic
- **Capstone 2:** Interfaces, inheritance, polymorphism, dependency design, basic architecture
- **Capstone 3:** Orchestration patterns, multi-criteria routing, fee calculation, enums, filtering and selection logic

## Project Structure

```
src/main/java/com/paynestsystem/
├── domain/           # Core business objects (Product, Customer, OrderItem, Order)
├── service/          # Business logic (OrderService)
├── payment/          # Payment implementations (PaymentMethod, CardPayment, EftPayment, WalletPayment, PaymentProcessor)
├── orchestration/    # Payment orchestration (Region, PaymentProvider, FeeCalculator, PaymentOrchestrator)
└── app/              # CLI application entry point (PayNestApplication)
```
