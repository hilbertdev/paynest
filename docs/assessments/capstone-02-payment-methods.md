# Capstone 2: Unified checkout across payment rails

## Scenario / Fictional Company Context

PayNest’s pilot merchants loved the **order desk** (Capstone 1). Now they ask for **checkout**: pay by card, by bank transfer (EFT), or from an in-app wallet. Operations hates maintaining three separate “if card / else if EFT” codepaths—they want **one checkout funnel** that accepts any supported rail today and can add another rail **without rewriting order logic**.

You remain on the backend team. Product’s pitch is: “Same order total everywhere; only the payment rail changes.”

## Business Problem

Hard-coded branching per payment type **does not scale**: each new rail duplicates validation messaging, logging, and failure handling. PayNest needs a **payment abstraction** so `Order` (or the checkout orchestrator) depends on a **single contract**—process this Rand amount—while concrete rails encapsulate rail-specific behaviour (labels, simulated processing). The business still needs a **clear console narrative** when payment succeeds or fails so support staff can replay demos.

## System Requirements

This capstone starts from the order classes in Capstone 1. If you are new to Java, think of the work as adding one interface, several small payment classes, and one checkout flow that uses the interface.

**Polymorphic payment processing**

- Define a `PaymentMethod` interface to represent "how the customer pays".
- Add a method such as `boolean processPayment(double amount)` to the interface. Every payment type must implement this method.
- Add a method such as `String getPaymentType()` so each payment type can describe itself in console output.
- Create **at least three** classes that implement `PaymentMethod`, for example `CardPayment`, `EftPayment`, and `WalletPayment`.
- In each concrete payment class, print a short message that makes it clear which rail is being used.
- Process a payment for the **order grand total** using `processPayment(double amount)` semantics aligned with the starter implementation.

**Checkout orchestration**

- Define one checkout path that accepts a `PaymentMethod` parameter instead of hard-coding a specific payment class.
- During checkout, calculate the total from the order's line items using the same business meaning as Capstone 1.
- Pass that calculated amount into the selected `PaymentMethod`.
- Successful checkout prints confirmation tying **payment rail**, **amount**, and **order completion** (behaviour consistent with `PayNestApplication` expectations).

**Integration surface**

- Create or complete a `PaymentProcessor` class that has a method accepting `PaymentMethod` and `amount`.
- `PaymentProcessor` or `Order#checkout` must accept **any** `PaymentMethod` implementation without recompilation when a new rail is added.
- To check your design, imagine adding `BuyNowPayLaterPayment`. You should need a new class plus demo wiring, not changes to the order total calculation.

**Demonstration**

- In `PayNestApplication`, build the Capstone 1 order first.
- Choose one concrete payment type, for example `new CardPayment()`.
- Pass that object into the checkout flow through the `PaymentMethod` interface type.
- Run the application and confirm the output shows the order summary, payment rail, payment amount, and order completion.

## Starter Code / Example Scaffold

If you are starting from the Capstone 1 order classes, use the example below as a small scaffold for Capstone 2. The package names match the PayNest repository layout. You may copy these snippets into the listed files, then extend them with your own validation, tests, and console messages.

**1. Define the payment contract**

```java
// src/main/java/com/paynestsystem/payment/PaymentMethod.java
package com.paynestsystem.payment;

public interface PaymentMethod {
    boolean processPayment(double amount);

    String getPaymentType();
}
```

**2. Implement one rail first, then repeat the pattern**

```java
// src/main/java/com/paynestsystem/payment/CardPayment.java
package com.paynestsystem.payment;

public class CardPayment implements PaymentMethod {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("Processing card payment for R" + String.format("%.0f", amount));
        return true; // Simulated payment for the capstone demo.
    }

    @Override
    public String getPaymentType() {
        return "CARD";
    }
}
```

Create `EftPayment` and `WalletPayment` in the same package by implementing the same two methods. Each class should own its rail-specific label and message; the checkout flow should not use `if` or `switch` statements to decide how a rail works.

**3. Keep checkout code pointed at the interface**

```java
// src/main/java/com/paynestsystem/payment/PaymentProcessor.java
package com.paynestsystem.payment;

public class PaymentProcessor {
    public boolean processPayment(PaymentMethod method, double amount) {
        boolean success = method.processPayment(amount);

        if (success) {
            System.out.println("Payment successful via " + method.getPaymentType());
            System.out.println("Amount: R" + String.format("%.0f", amount));
        } else {
            System.out.println("Payment failed via " + method.getPaymentType());
        }

        return success;
    }
}
```

Your `Order#checkout(PaymentMethod paymentMethod)` method can calculate the order total once and pass it to `PaymentProcessor`. The important design point is that `Order` accepts the `PaymentMethod` interface, not `CardPayment`, `EftPayment`, or `WalletPayment` directly.

**4. Wire the example in the application**

```java
PaymentMethod paymentMethod = new CardPayment();
order.checkout(paymentMethod);
```

To demonstrate polymorphism, change only the construction line to `new EftPayment()` or `new WalletPayment()`. The rest of the checkout code should stay the same.

**5. Example test shape**

```java
// src/test/java/com/paynestsystem/payment/PaymentMethodTest.java
package com.paynestsystem.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PaymentMethodTest {
    @Test
    void checkoutCanUseDifferentPaymentMethods() {
        PaymentMethod card = new CardPayment();
        PaymentMethod wallet = new WalletPayment();

        assertTrue(card.processPayment(12400));
        assertTrue(wallet.processPayment(12400));
        assertEquals("CARD", card.getPaymentType());
        assertEquals("WALLET", wallet.getPaymentType());
    }
}
```

This test is intentionally small. Stronger submissions also verify that the amount passed to payment equals `order.calculateTotal()`.

## Suggested Implementation Order for Beginners

1. Confirm Capstone 1 can create an order and calculate the correct total.
2. Create the `PaymentMethod` interface with `processPayment(double amount)` and `getPaymentType()`.
3. Create `CardPayment`, implement `PaymentMethod`, and return a clear payment type label.
4. Repeat the same pattern for `EftPayment` and `WalletPayment`.
5. Create or update `PaymentProcessor` so it works with the interface, not with one concrete class.
6. Add checkout code that calculates the order total once and sends that total to the chosen payment method.
7. Update `PayNestApplication` to demonstrate the full flow with one payment method.
8. Add or update tests to run checkout with at least two different `PaymentMethod` implementations.

## Technical Constraints

- **Java 21**, **Maven**, plain Java—**no** payment gateway SDKs or HTTP calls in this capstone unless your instructor extends the brief.
- **Interfaces & polymorphism** are mandatory; avoid large `switch` on string payment type in core checkout logic (small switches inside concrete rails are acceptable if justified).
- Preserve Capstone 1 domain semantics unless you explicitly migrate with tests.

## Business Rules

- **Amount charged** at checkout must equal the order total produced by the order model for that checkout invocation (no silent discounts).
- Each rail exposes a **stable human-readable type label** suitable for logs and receipts.
- **`processPayment`** returns or signals success/failure consistently with the starter contract; document assumptions if you extend return types (prefer staying within the existing API unless approved).
- **Adding a new rail** should require **new class + wiring**, not editing `Order`’s core arithmetic.

## Expected Deliverables

- Source implementing polymorphic payments and integrated checkout.
- **`mvn test` passes**; include tests that exercise checkout with **at least two** different payment implementations (polymorphism actually used).
- Short note (submission README or PDF): **one paragraph** on why interfaces beat a single mega-method here; optional **class diagram** showing `PaymentMethod` and implementations.
- Demo instructions matching programme standards.

## Assessment Framing

Reviewers reward **dependency direction**: domain and checkout depend on abstractions, not concrete card classes. They will ask: “If we add `BuyNowPayLaterPayment` next sprint, what files change?” The best answers touch **one new class** plus composition root / demo wiring—not `Order` internals.

## Rubric

| Category | Weight | What reviewers look for |
|----------|--------|-------------------------|
| Architecture & polymorphism | 25% | Clean `PaymentMethod` contract; checkout depends on abstraction; minimal branching at call sites. |
| Correctness & business rules | 30% | Correct total flows into payment; rail labels coherent; success path matches domain expectations. |
| Testing & verification | 20% | Tests prove polymorphic behaviour (multiple rails); regression protection on totals + checkout. |
| Code quality & maintainability | 15% | Readable implementations per rail; no duplicated total calculation paths. |
| Documentation & communication | 10% | Brief design rationale; clear run instructions; diagram optional but rewarded if precise. |

**Distinction-level signal:** new payment type added in a **timed extension exercise** with minimal edits outside the new class and wiring.
