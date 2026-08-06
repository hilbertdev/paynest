# Live session: OOP Foundations — Preparing for Capstone 2

**Audience:** Beginner Java developers who completed Capstone 1 (classes, constructors, `List`) but have not yet formally learned interfaces, polymorphism, or SOLID.  
**Duration:** ~90–120 minutes (lecture) + appendices for lab  
**Mode:** Concept lecture grounded in the **existing** PayNest codebase (`Order`, `PaymentMethod`, Card/EFT/Wallet).

## Deck

- [oop-foundations-capstone-02.pptx](oop-foundations-capstone-02.pptx) — **student handout** (no speaker notes); import into Google Slides via **File → Import slides** or upload the PPTX
- [oop-foundations-capstone-02.md](oop-foundations-capstone-02.md) — Marp / Markdown source (also student-facing; no speaker notes)

### Rebuild PPTX

```bash
/Users/hilbertmu/source/PayNest/tmp/slides-venv/bin/python \
  /Users/hilbertmu/source/PayNest/tmp/oop-foundations-capstone-02/build_deck.py
```

### Import into Google Slides

1. Upload `oop-foundations-capstone-02.pptx` to Google Drive, or in Slides use **File → Import slides**.
2. Fonts: Poppins / Lato (same family as other Capstone decks). Google may substitute if missing — content remains editable.

## Slide map (~24 teaching + appendix)

| Part | Slides | Focus |
|------|--------|--------|
| 1 Introduction | 1–3 | Title, outcomes, why OOP |
| 2 Pillars | 4 | Four pillars preview |
| 3 Encapsulation | 5–9 | Private fields, bad/good, quiz |
| 4 Transition | 10 | Into polymorphism |
| 5 Polymorphism | 11–17 | Interface, rails, dispatch, checks |
| 6 Why it matters | 18–21 | Mechanism vs SOLID, OCP, DIP |
| 7 Capstone preview | 22–24 | Architecture, BNPL, takeaways |
| Appendix | A–E | Live coding, exercises, misconceptions, quiz, homework |

## Prep checklist

1. Java 21 + Maven; repo builds:

```bash
mvn test
mvn exec:java
```

2. Have these files open:

- `src/main/java/com/paynestsystem/domain/Order.java`
- `src/main/java/com/paynestsystem/domain/OrderItem.java`
- `src/main/java/com/paynestsystem/domain/Product.java`
- `src/main/java/com/paynestsystem/payment/PaymentMethod.java`
- `src/main/java/com/paynestsystem/payment/CardPayment.java` (and Eft/Wallet)
- `src/main/java/com/paynestsystem/payment/PaymentProcessor.java`
- `src/main/java/com/paynestsystem/app/PayNestApplication.java`

3. Scratch buffer ready for the **BadOrder** / if/else anti-patterns — do **not** commit them.
4. Optional: Capstone 2 brief on a second screen.

## Teaching stance

- Prefer PayNest examples over textbook `Animal` / `Shape` hierarchies.
- Capstone 2 rails are **interface siblings**, not an `extends` tree — call that out.
- Be honest: `new PaymentProcessor()` inside `Order.checkout` is a staged DIP trade-off.
- Polymorphism is a **mechanism**; OCP/DIP are **principles** it enables.

## Related material

- Capstone 2 brief: [../../assessments/capstone-02-payment-methods.md](../../assessments/capstone-02-payment-methods.md)
- SE walkthrough (design pain → interfaces): [../capstone-02-design-to-interfaces/](../capstone-02-design-to-interfaces/)
