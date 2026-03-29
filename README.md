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

# Run unit tests
mvn test

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

**Adaptive Payment Routing & Risk Engine (Skeleton)**

Capstone 3 adds a **barebones scaffolding** for routing transactions to payment providers and assessing risk. **It is not a complete solution:** methods are stubs or placeholders with `TODO` comments so students can implement policy, thresholds, and integration.

**Core concepts:**

- **Interfaces:** `PaymentProvider`, `RoutingRule`, `RiskEvaluator`, `RoutingEngine` — contracts only; students fill in behavior.
- **Routing:** `DefaultRoutingEngine` + `RouteDecision` — loop shell and placeholder decision; students implement rule ordering, provider selection, and fallback.
- **Rules:** `AbstractRoutingRule` and example rules (`AmountRoutingRule`, `BankSupportRule`, `FallbackRule`) — default `matches` returns false; students encode real conditions.
- **Providers:** `BasePaymentProvider`, `ProviderA`, `ProviderB` — placeholder `process` implementations.
- **Risk:** `RiskLevel` enum and `BasicRiskEvaluator` — default `LOW`; students add amount/frequency logic.
- **Config & audit:** `RoutingConfig` for priority/thresholds structure; `DecisionLogger` prints to the console for now.

**New domain:** `Transaction` (`amount`, `bank`, `timestamp`) for routing and risk evaluation.

Run tests with `mvn test`. The main entry point (`PayNestApplication`) still demonstrates Capstones 1–2; you can wire `DefaultRoutingEngine` and sample lists in your own `main` or tests as you explore Capstone 3.

## Learning Objectives

- **Capstone 1:** Classes, objects, constructors, encapsulation, collections (`List`), basic business logic
- **Capstone 2:** Interfaces, inheritance, polymorphism, dependency design, basic architecture
- **Capstone 3:** Layered design, strategy-style routing, risk scoring, configuration placeholders, and extending interfaces without a finished rules engine

## Project Structure

```
src/main/java/com/paynestsystem/
├── domain/      # Core business objects (Product, Customer, OrderItem, Order, Transaction)
├── service/     # Business logic (OrderService)
├── payment/     # Payment implementations (PaymentMethod, CardPayment, EftPayment, WalletPayment, PaymentProcessor)
├── routing/     # RoutingEngine, DefaultRoutingEngine, RouteDecision, DecisionLogger
├── rules/       # RoutingRule, AbstractRoutingRule, example rules
├── providers/   # PaymentProvider, BasePaymentProvider, ProviderA, ProviderB
├── risk/        # RiskLevel, RiskEvaluator, BasicRiskEvaluator
├── config/      # RoutingConfig (placeholder)
└── app/         # CLI application entry point (PayNestApplication)
```
