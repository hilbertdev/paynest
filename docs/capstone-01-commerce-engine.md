# Capstone 1: Merchant order desk and catalogue engine

## Scenario / Fictional Company Context

**PayNest** is an early-stage South African fintech building lightweight commerce tools for **small merchants** who sell hardware and accessories online and at markets. They cannot afford Shopify-scale subscriptions but still need **consistent pricing**, **order totals**, and **customer-linked receipts** before any payment integration exists.

You join as a **junior backend engineer** on contract. Your lead engineer tells you: “Ship something we can demo to merchants next week—no frameworks, just solid Java objects we can extend later.” Currency in demos uses **Rand (R)** formatting consistent with the starter codebase.

## Business Problem

Merchants currently patch together spreadsheets and WhatsApp messages. That breaks down under growth: line totals disagree with invoices, staff adds duplicate products by mistake, and there is **no single code path** that computes “what the customer owes.” PayNest needs a **minimal commerce kernel**: products with prices, customers, orders with line items, and a **trustworthy order total** printed as a human-readable summary suitable for a CLI demo or a future API.

## System Requirements

If you are new to Java, read each requirement as a small class or method to build. Complete the classes in the order below before worrying about polish.

**Product catalogue**

- Define a `Product` class to represent something the merchant can sell.
- Give each `Product` three private fields: an `id`, a display `name`, and a unit `price` in Rand.
- Add a constructor so a product can be created in one line, for example `new Product(1, "Laptop", 12000)`.
- Add getter methods so other classes can read the product name and price when building an order summary.

**Customer identity**

- Define a `Customer` class for the person buying from the merchant.
- Give each `Customer` an `id`, `name`, and contact detail such as `email`.
- Add a constructor and getters so the order can print the customer's name in the receipt header.

**Order lifecycle (read-only checkout for Capstone 1)**

- Define an `Order` class to create an empty order for a customer.
- Store the order's `id`, the `Customer`, and a `List<OrderItem>` for the products added to the order.
- Define an `OrderItem` class to link one `Product` with one `quantity`.
- In `OrderItem`, add a method such as `calculateTotal()` that returns `product price * quantity`.
- In `Order`, add a method such as `addItem(Product product, int quantity)` to add a new `OrderItem` to the list. Quantities must be positive integers for this programme unless your instructor approves otherwise.
- In `Order`, add a method such as `calculateTotal()` that loops through all order items and adds their line totals together.
- In `Order`, produce an **order summary** that lists each line (product name, quantity, line subtotal) and a **grand total** equal to the sum of line subtotals.

**Demonstration workflow**

- In `PayNestApplication`, create at least two sample `Product` objects.
- Create one sample `Customer`.
- Use an `OrderService` class, or the `Order` constructor directly if your instructor allows it, to create an order for that customer.
- Add at least two line items to the order, including one product with quantity greater than `1`.
- Print the order summary so a reviewer can see the customer, each line item, and the grand total.

**Operational clarity**

- Someone reading console output can reconcile totals manually without hidden magic numbers.

## Suggested Implementation Order for Beginners

1. Create or complete the simple data classes first: `Product` and `Customer`.
2. Create `OrderItem` and test that one line subtotal is calculated correctly.
3. Create `Order` with an empty `List<OrderItem>` in its constructor.
4. Add `Order#addItem(...)`, then confirm that the list grows when items are added.
5. Add `Order#calculateTotal()` by looping through the list and summing each `OrderItem` total.
6. Add `Order#printSummary()` only after the calculations are correct.
7. Use `PayNestApplication` to manually run the full flow from product creation to printed summary.

## Technical Constraints

- **Language / tooling:** Java **21**, Apache **Maven** for build and tests.
- **Architecture:** Plain Java—**no** Spring, Jakarta EE app servers, or database for this capstone unless your instructor explicitly varies the brief.
- **Structure:** Follow package conventions already present under `com.paynestsystem` (`domain`, `service`, `app`).
- **Collections:** Use appropriate JDK collections (for example `List`) for order lines; avoid parallel streams unless justified.
- **Testing:** Project uses JUnit 5; add or extend tests where they strengthen correctness (totals, edge cases).

## Business Rules

- **Line subtotal** for a product line is `unitPrice * quantity` using `double` arithmetic as in the starter code; document any rounding policy if you introduce one (otherwise match existing behaviour).
- **Grand total** must equal the sum of line subtotals for the order contents returned by your domain API (same definition used by `printSummary` or equivalent).
- **Quantities** added to an order must be valid for your implementation (typically `> 0`); reject or guard invalid adds consistently.
- **Encapsulation:** Domain objects should not expose mutable internals in ways that let callers corrupt totals silently (for example uncontrolled modification of backing collections).

## Expected Deliverables

- **Source code** implementing the commerce flow in the repository layout above.
- **`mvn test` passes** on your branch (including any new tests you add).
- **Short setup note** (README fragment or submission comment): how to run the demo (`mvn exec:java` or equivalent) and what output reviewers should see.
- **Optional but valued:** one **simple diagram** (sequence or component) showing `OrderService` → domain objects → summary output.
- **Submission artefact** per programme rules (zip, Git tag, or LMS upload).

## Assessment Framing

Strong submissions **read like production-minded junior work**: clear naming, small cohesive classes, and totals that survive scrutiny. Reviewers will look for **deliberate modelling** (why `OrderItem` exists, how `Order` owns its lines) rather than a single “god class.” Edge cases (empty order, single line, multiple quantities) should behave sensibly or fail loudly—not quietly wrong totals.

You must be able to explain **why** your design can add new product fields later without rewriting checkout.

## Rubric

| Category | Weight | What reviewers look for |
|----------|--------|-------------------------|
| Architecture & domain modelling | 25% | Clear separation between `Product`, `Customer`, `Order`, `OrderItem`; coherent `OrderService` API; extensibility without breaking callers. |
| Correctness & business rules | 30% | Line subtotals and grand total match the stated rules; quantities validated; summary matches internal computation. |
| Testing & verification | 15% | Tests cover totals and non-trivial cases; `mvn test` green; failures would catch regressions in arithmetic or collection behaviour. |
| Code quality & maintainability | 20% | Readable code, consistent style, sensible encapsulation, no dead experimental paths left in `main`. |
| Documentation & communication | 10% | Setup/run instructions; diagram or short note if submitted; comments where business rules are non-obvious. |

**Pass expectation:** no critical correctness failures in totals; rubric average aligned with your programme’s grade boundaries.
