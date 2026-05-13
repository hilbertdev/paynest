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
