# Live session: Capstone 2 — From design pain to interfaces

**Audience:** Second-year CS students who completed Capstone 1 (orders, totals, basic OOP, Maven, JUnit).  
**Duration:** 2–3 hours  
**Mode:** Software engineering live walkthrough of the **existing** Capstone 2 solution (do not strip payment classes). Show the if/else anti-pattern only as deliberate contrast.

## Deck

- [capstone-02-se-lesson.pptx](capstone-02-se-lesson.pptx) — import into Google Slides via **File → Import slides** or upload the PPTX.
- Rebuild from source:

```bash
cd tmp/capstone-02-se-lesson
python3 -m venv .venv          # once
.venv/bin/pip install python-pptx
.venv/bin/python build_deck.py
```

Fonts: Poppins / Lato (same as Capstone 1 intro). Google Slides may substitute if fonts are missing — content remains editable text boxes.

**Speaker notes:** Each teaching slide includes the full lecturer script (objective, why, analogy, live coding, exercise, mistakes, questions, transition). Use **View → Show speaker notes** in Google Slides or Presenter view in PowerPoint.

## Timing

| Block | Time | Focus |
|-------|------|--------|
| 0 | ~15 min | Capstone 1 recap — objects students already own |
| 1 | ~25 min | Why software changes; tight coupling / if/else pain |
| 2 | ~20 min | Responsibility, cohesion, coupling, composition |
| 3 | ~25 min | Abstractions; `PaymentMethod`; program to abstractions / DIP |
| 4 | ~30 min | Polymorphism, dynamic dispatch, OCP |
| 5 | ~20 min | `PaymentProcessor`, `Order.checkout`, SoC, SRP, DRY |
| 6 | ~20 min | Business rules; unit + regression tests |
| 7 | ~15 min | Rubric mapping + distinction BNPL signal |
| Exercises / breaks | ~20 min | Embedded in blocks |

## Prep checklist

1. Clone / open the PayNest repo; Java 21 + Maven available.
2. Confirm Capstone 1–2 demo runs:

```bash
mvn test
mvn exec:java
```

3. Open the deck; enable speaker notes.
4. Have a scratch buffer ready for the **anti-pattern** `checkout(String type)` — do **not** commit it into `Order.java`.
5. Optional whiteboard for BNPL “files to touch” lists (Block 1 vs Block 4).

## Suggested file open order

1. [`PayNestApplication.java`](../../../src/main/java/com/paynestsystem/app/PayNestApplication.java) — Capstone 1 then Capstone 2 blocks  
2. [`Order.java`](../../../src/main/java/com/paynestsystem/domain/Order.java) — totals, encapsulation, `checkout`  
3. Scratch anti-pattern if/else (contrast only)  
4. [`PaymentMethod.java`](../../../src/main/java/com/paynestsystem/payment/PaymentMethod.java)  
5. `CardPayment` / `EftPayment` / `WalletPayment`  
6. [`PaymentProcessor.java`](../../../src/main/java/com/paynestsystem/payment/PaymentProcessor.java)  
7. [`OrderTest.java`](../../../src/test/java/com/paynestsystem/domain/OrderTest.java) — C1 regression + C2 test gap  
8. Brief: [`docs/assessments/capstone-02-payment-methods.md`](../../assessments/capstone-02-payment-methods.md)  
9. Rubric: [`docs/capestone-rubrics/capstone-02-rubric.csv`](../../capestone-rubrics/capstone-02-rubric.csv)

## Teaching stance

- Start from the **business change request**, not the `interface` keyword.
- Capstone 2 solution code is the **destination**; students should feel why each piece exists.
- Honest note: `new PaymentProcessor()` inside `Order.checkout` is a Capstone 2 teaching trade-off; later capstones push dependencies further out.

## Related material

- Capstone 2 brief: [capstone-02-payment-methods.md](../../assessments/capstone-02-payment-methods.md)
- DDD / noun–verb session: [../ddd-domain-analysis/](../ddd-domain-analysis/)
- Capstone 1 intro deck pattern: [`tmp/capstone-01-intro/`](../../../tmp/capstone-01-intro/)
