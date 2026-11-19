package com.paynestsystem.debrief;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * CAPSTONE 3 DEBRIEF - one file, every common mark-loser fixed, with comments.
 * <p>
 * How to use this in the session: read top to bottom. Each "ISSUE n" block is
 * a thing that cost marks across the cohort, shown fixed. Run {@link #main}
 * at the end to see every fix prove itself in the console.
 * <p>
 * Everything is nested in this one file on purpose, so it cannot clash with
 * the classes already in com.paynestsystem.payment. Your own repo should have
 * ONE top-level class per file - this is a teaching file, not a layout to copy.
 *
 * <pre>
 * ISSUE 1  PaymentMethod is an INTERFACE (what the brief asks for)
 * ISSUE 2  Each rail owns a business rule (the biggest chunk of marks)
 * ISSUE 3  A failed payment never says "order complete"
 * ISSUE 4  Validate the amount once, not in every rail
 * ISSUE 5  Money: BigDecimal inside, double only at the boundary
 * ISSUE 6  Calculate the total once and reuse it (receipt = amount charged)
 * ISSUE 7  Tests that check the console and the amount, with two rails
 * ISSUE 8  A new rail is one new class (BuyNowPayLater), Order never changes
 * ISSUE 9  README line: why an interface beats one mega-method
 * ISSUE 10 Maven and Git hygiene (comment block, nothing to compile)
 * BRIDGE   Capstone 4 preview: choosing a rail at runtime (strategy pattern)
 * </pre>
 *
 * Java 8+ syntax only, so it compiles on Java 21 and anything older.
 */
public final class Capstone3Debrief {

    private Capstone3Debrief() {
    }

    // =====================================================================
    // ISSUE 1: PaymentMethod is an interface
    // =====================================================================
    //
    // The brief says: "Define a PaymentMethod interface". Marking follows
    // the brief, so this is an interface with exactly the two methods the
    // starter code gives you.
    //
    // DEFINITION - interface: a contract. It says WHAT a payment method can
    // do (process an amount, name itself) and says nothing about HOW.
    //
    // DEFINITION - polymorphism: code that talks to the contract
    // (PaymentMethod) works with any class that signs it, without knowing
    // which one it got.
    //
    // "But I want to share code between rails." Fine - you do not need an
    // abstract parent for that. Two tools that keep the interface:
    //   (a) a static helper that every rail can call (see ISSUE 4), and
    //   (b) a default method on the interface (see label() below).
    // An abstract parent class is a legitimate design in general, but the
    // brief asked for an interface, so submit the interface.
    interface PaymentMethod {

        /**
         * @param amount the Rand amount to charge
         * @return true if the rail accepted the payment, false if it declined
         */
        boolean processPayment(double amount);

        /** Stable label for logs and receipts, e.g. "CARD". */
        String getPaymentType();

        /**
         * A default method: shared behaviour that lives ON the interface.
         * Every rail gets it for free and may override it.
         */
        default String label() {
            return "[" + getPaymentType() + "]";
        }
    }

    // =====================================================================
    // ISSUE 2: each rail owns a business rule
    // =====================================================================
    //
    // Most submissions had rails that just "return true". The brief's
    // "rail-specific behaviour" means each rail can say NO for its own
    // reason. The rule lives ON the rail, never in the checkout:
    //   - card:   a per-payment limit
    //   - EFT:    needs a bank reference
    //   - wallet: cannot spend more than the balance
    //   - BNPL:   a credit limit (ISSUE 8)
    // A decline is a normal business outcome, so we return false.
    // We do NOT throw an exception for "balance too low".

    static class CardPayment implements PaymentMethod {
        private final double limitPerPayment;

        CardPayment(double limitPerPayment) {
            this.limitPerPayment = limitPerPayment;
        }

        @Override
        public boolean processPayment(double amount) {
            // Rail-specific message: support staff can see WHICH rail ran.
            System.out.println(label() + " Processing card payment for R" + rands(amount));
            if (amount > limitPerPayment) {
                System.out.println(label() + " Declined: R" + rands(amount)
                        + " is above the card limit of R" + rands(limitPerPayment));
                return false;
            }
            return true;
        }

        @Override
        public String getPaymentType() {
            return "CARD";
        }
    }

    static class EftPayment implements PaymentMethod {
        private final String bankReference;

        EftPayment(String bankReference) {
            this.bankReference = bankReference;
        }

        @Override
        public boolean processPayment(double amount) {
            System.out.println(label() + " Processing EFT for R" + rands(amount));
            // The EFT rule: no reference, no transfer.
            if (bankReference == null || bankReference.trim().isEmpty()) {
                System.out.println(label() + " Declined: a bank reference is required");
                return false;
            }
            System.out.println(label() + " Bank reference: " + bankReference);
            return true;
        }

        @Override
        public String getPaymentType() {
            return "EFT";
        }
    }

    static class WalletPayment implements PaymentMethod {
        // The wallet's own state. No other rail has a balance, so no other
        // class manages it. Private: changed only by paying.
        private double balance;

        WalletPayment(double openingBalance) {
            this.balance = openingBalance;
        }

        @Override
        public boolean processPayment(double amount) {
            System.out.println(label() + " Processing wallet payment for R" + rands(amount));
            if (amount > balance) {
                System.out.println(label() + " Declined: R" + rands(amount)
                        + " is more than the balance of R" + rands(balance));
                return false; // balance is left untouched on a decline
            }
            balance -= amount;
            System.out.println(label() + " Paid. Remaining balance: R" + rands(balance));
            return true;
        }

        double getBalance() {
            return balance;
        }

        @Override
        public String getPaymentType() {
            return "WALLET";
        }
    }

    // =====================================================================
    // ISSUE 4: validate the amount once
    // =====================================================================
    //
    // Many repos repeated "if (amount <= 0)" inside every rail. That is the
    // same rule written three times - and a fourth rail will forget it.
    // Put the SHARED rule in one place that the checkout always runs
    // BEFORE any rail. Rules only one rail understands stay on that rail.
    static final class AmountRules {
        private AmountRules() {
        }

        static boolean isValid(double amount) {
            return amount > 0 && !Double.isNaN(amount) && !Double.isInfinite(amount);
        }
    }

    // =====================================================================
    // ISSUE 3 + ISSUE 4: the processor reports success AND failure
    // =====================================================================
    //
    // PaymentProcessor depends on the interface only - it has no idea which
    // rail it holds. That is the "dependency direction" reviewers look for:
    // the checkout depends on the abstraction, not on CardPayment.
    static class PaymentProcessor {

        boolean processPayment(PaymentMethod method, double amount) {
            // ISSUE 4: the shared rule, written once.
            if (!AmountRules.isValid(amount)) {
                System.out.println("Rejected: amount must be greater than R0");
                return false;
            }

            boolean success = method.processPayment(amount);

            // ISSUE 3: both outcomes get a console line. A failed payment
            // must be visible, not silent.
            if (success) {
                System.out.println("Payment successful via " + method.getPaymentType());
                System.out.println("Amount: R" + rands(amount));
            } else {
                System.out.println("Payment failed via " + method.getPaymentType());
            }
            return success;
        }
    }

    // =====================================================================
    // ISSUE 5 + ISSUE 6: money type and a single total
    // =====================================================================
    //
    // DEFINITION - floating point: double stores numbers in binary, so
    // some decimals (0.1, 0.2) cannot be stored exactly. 0.1 + 0.2 is
    // 0.30000000000000004. For money, that is a bug waiting to happen.
    //
    // DEFINITION - BigDecimal: exact decimal arithmetic. Use it for money.
    //
    // The brief's contract is processPayment(double amount), and it says
    // to stay inside the existing API unless approved. So:
    //   - do all the arithmetic in BigDecimal (inside the Order), and
    //   - convert to double ONCE, at the boundary where the contract needs
    //     it, and refuse to proceed if the conversion would change the value.
    static final class Money {
        private Money() {
        }

        static BigDecimal rands(String text) {
            return new BigDecimal(text).setScale(2, RoundingMode.HALF_UP);
        }

        /**
         * The boundary conversion. BigDecimal.valueOf(double) uses the
         * shortest text that round-trips, so if converting to double and back
         * gives the same number, nothing was lost. If not, we say so.
         */
        static double toContractDouble(BigDecimal amount) {
            double asDouble = amount.doubleValue();
            if (BigDecimal.valueOf(asDouble).compareTo(amount) != 0) {
                throw new IllegalArgumentException(
                        "Amount R" + amount.toPlainString()
                                + " cannot be represented accurately as a double");
            }
            return asDouble;
        }
    }

    static class Line {
        final String name;
        final BigDecimal unitPrice;
        final int quantity;

        Line(String name, BigDecimal unitPrice, int quantity) {
            // The domain validates itself: bad lines never enter an order.
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("name is required");
            }
            if (unitPrice == null || unitPrice.signum() < 0) {
                throw new IllegalArgumentException("unit price cannot be negative");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be greater than 0");
            }
            this.name = name;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        BigDecimal total() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    static class Order {
        private final int id;
        private final List<Line> lines = new ArrayList<>();

        Order(int id) {
            this.id = id;
        }

        void addLine(Line line) {
            lines.add(line);
        }

        /**
         * ISSUE 6: THE ONE place the total is calculated. The receipt, the
         * checkout and the tests all call this - nothing recalculates it.
         * (Cohort pattern: Order and an OrderSummary class each summed the
         * lines. Two calculations can drift apart. One cannot.)
         */
        BigDecimal calculateTotal() {
            BigDecimal total = BigDecimal.ZERO;
            for (Line line : lines) {
                total = total.add(line.total());
            }
            return total.setScale(2, RoundingMode.HALF_UP);
        }

        /**
         * The checkout depends on the PaymentMethod INTERFACE, never on a
         * concrete rail. No if/else or switch on the rail type anywhere.
         *
         * @return true only if the order was actually paid for
         */
        boolean checkout(PaymentMethod paymentMethod) {
            // Guard clauses: fail early with a clear message.
            if (paymentMethod == null) {
                System.out.println("Order " + id + " NOT completed: no payment method given");
                return false;
            }
            if (lines.isEmpty()) {
                System.out.println("Order " + id + " NOT completed: the order has no items");
                return false;
            }

            // ISSUE 6: calculate once, reuse for receipt AND for payment.
            BigDecimal total = calculateTotal();
            if (total.signum() <= 0) {
                System.out.println("Order " + id + " NOT completed: total is R0");
                return false;
            }

            printReceipt(total);

            // ISSUE 5: the single BigDecimal -> double conversion.
            double amount;
            try {
                amount = Money.toContractDouble(total);
            } catch (IllegalArgumentException e) {
                System.out.println("Order " + id + " NOT completed: " + e.getMessage());
                return false;
            }

            boolean paid = new PaymentProcessor().processPayment(paymentMethod, amount);

            // ISSUE 3: "complete" is printed ONLY when payment succeeded.
            if (paid) {
                System.out.println("Order " + id + " completed: R" + total.toPlainString()
                        + " paid via " + paymentMethod.getPaymentType());
            } else {
                System.out.println("Order " + id + " NOT completed: payment via "
                        + paymentMethod.getPaymentType() + " did not go through");
            }
            return paid;
        }

        private void printReceipt(BigDecimal total) {
            System.out.println("Order Summary (order " + id + ")");
            for (Line line : lines) {
                System.out.println("  " + line.name + " x" + line.quantity
                        + " - R" + line.total().setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
            // The total printed here is the SAME object we charge.
            System.out.println("Total: R" + total.toPlainString());
        }
    }

    // =====================================================================
    // ISSUE 8: a new rail is one new class
    // =====================================================================
    //
    // The reviewer question: "If we add BuyNowPayLaterPayment next sprint,
    // what files change?" Right answer: ONE new class plus the demo wiring.
    // Order and PaymentProcessor above do not change by a single character.
    static class BuyNowPayLaterPayment implements PaymentMethod {
        private static final double CREDIT_LIMIT = 2000.0;

        @Override
        public boolean processPayment(double amount) {
            System.out.println(label() + " Processing buy-now-pay-later for R" + rands(amount));
            if (amount > CREDIT_LIMIT) {
                System.out.println(label() + " Declined: above the R"
                        + rands(CREDIT_LIMIT) + " credit limit");
                return false;
            }
            return true;
        }

        @Override
        public String getPaymentType() {
            return "BNPL";
        }
    }

    // =====================================================================
    // BRIDGE TO CAPSTONE 4: choosing a rail at runtime (strategy pattern)
    // =====================================================================
    //
    // PREVIEW ONLY - written from the syllabus theme "smart rails
    // selection", not from a released Capstone 4 brief.
    //
    // DEFINITION - strategy pattern: put each "way of deciding" behind an
    // interface and swap the decision in without touching the code that
    // uses it. You already did this once: PaymentMethod is a strategy for
    // HOW to pay. Capstone 4 asks a new question: WHICH rail should we use?
    // Same trick, one level up.
    interface RailSelectionStrategy {
        /** @return the rails to try, best first */
        List<PaymentMethod> orderedChoices(BigDecimal total, List<PaymentMethod> available);
    }

    /** Small amounts go to the wallet first; big ones go to EFT first. */
    static class AmountBasedSelection implements RailSelectionStrategy {
        private final BigDecimal threshold;

        AmountBasedSelection(BigDecimal threshold) {
            this.threshold = threshold;
        }

        @Override
        public List<PaymentMethod> orderedChoices(BigDecimal total, List<PaymentMethod> available) {
            String preferred = total.compareTo(threshold) <= 0 ? "WALLET" : "EFT";
            List<PaymentMethod> ordered = new ArrayList<>(available);
            // Preferred rail moves to the front; the rest keep their order.
            Collections.sort(ordered, (a, b) -> Boolean.compare(
                    !a.getPaymentType().equals(preferred), !b.getPaymentType().equals(preferred)));
            return ordered;
        }
    }

    /** Tries the strategy's rails in order until one accepts (a fallback). */
    static class SmartCheckout {
        private final RailSelectionStrategy strategy;

        SmartCheckout(RailSelectionStrategy strategy) {
            this.strategy = strategy;
        }

        boolean pay(Order order, List<PaymentMethod> available) {
            BigDecimal total = order.calculateTotal();
            for (PaymentMethod rail : strategy.orderedChoices(total, available)) {
                System.out.println("Trying " + rail.getPaymentType() + " ...");
                if (order.checkout(rail)) {
                    return true;
                }
            }
            System.out.println("All rails declined.");
            return false;
        }
    }

    // =====================================================================
    // ISSUE 9: the README paragraph (the brief asks for one)
    // =====================================================================
    //
    // "Why interfaces beat a single mega-method here" - say it in your own
    // words, but include these three ideas:
    //   1. One contract (PaymentMethod) lets Order and PaymentProcessor stay
    //      the same when a rail is added. A mega-method with if/else on the
    //      rail type must be edited, and re-tested, for every new rail.
    //   2. Each rail owns its own rule, so a bug in the wallet cannot break
    //      the card.
    //   3. Tests can swap in a fake rail and check the amount, without
    //      touching real rails.
    //
    // =====================================================================
    // ISSUE 10: Maven and Git hygiene (nothing to compile, just do it)
    // =====================================================================
    //
    // 1. Make Maven PROVE your tests run. "mvn test" is only evidence if you
    //    read the line  "Tests run: N, Failures: 0, Errors: 0"  and N is the
    //    number you expect. Pin the Surefire plugin (3.x) so JUnit 5 tests
    //    are discovered:
    //
    //      <plugin>
    //        <groupId>org.apache.maven.plugins</groupId>
    //        <artifactId>maven-surefire-plugin</artifactId>
    //        <version>3.2.5</version>
    //      </plugin>
    //
    //    Then run:  mvn clean test
    //
    // 2. Java 21 in the pom (the brief says Java 21):
    //
    //      <maven.compiler.source>21</maven.compiler.source>
    //      <maven.compiler.target>21</maven.compiler.target>
    //
    // 3. Never commit compiled output. Add "target/" to .gitignore, and if
    //    it is already tracked, untrack it:
    //
    //      git rm -r --cached target
    //      git commit -m "Stop tracking target/"
    //
    // 4. Git saves in three steps: save the file, then "git add", then
    //    "git commit". "git status" shows what is not saved yet. Saved in
    //    the editor is not the same as committed.

    // =====================================================================
    // ISSUE 7: tests that check the console and the amount
    // =====================================================================
    //
    // These are plain self-checks so this file runs with no libraries.
    // In your repo, write the same ideas as JUnit 5 @Test methods.
    //
    // A recording rail is a FAKE: a PaymentMethod that remembers what it was
    // asked to charge. It proves the amount passed to payment equals
    // order.calculateTotal(), and it proves Order accepts ANY implementation.
    static class RecordingPayment implements PaymentMethod {
        double lastAmount = -1;
        int calls = 0;

        @Override
        public boolean processPayment(double amount) {
            calls++;
            lastAmount = amount;
            return true;
        }

        @Override
        public String getPaymentType() {
            return "RECORDER";
        }
    }

    /** Runs a block and returns everything it printed (console capture). */
    private static String capture(Runnable block) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true));
        try {
            block.run();
        } finally {
            System.setOut(original);
        }
        return new String(buffer.toByteArray());
    }

    private static int checks = 0;

    private static void check(boolean condition, String what) {
        checks++;
        if (!condition) {
            throw new AssertionError("FAILED: " + what);
        }
        System.out.println("  ok  " + what);
    }

    private static Order sampleOrder() {
        Order order = new Order(1001);
        order.addLine(new Line("Notebook", Money.rands("49.99"), 3)); // 149.97
        order.addLine(new Line("Pen", Money.rands("12.50"), 2));      // 25.00
        return order;                                                 // 174.97
    }

    public static void main(String[] args) {
        System.out.println("== ISSUE 1/8: one checkout, four rails, same Order code ==");
        List<PaymentMethod> rails = Arrays.asList(
                new CardPayment(50000), new EftPayment("INV-1001"),
                new WalletPayment(1000), new BuyNowPayLaterPayment());
        for (PaymentMethod rail : rails) {
            check(sampleOrder().checkout(rail), rail.getPaymentType() + " accepted R174.97");
        }

        System.out.println("== ISSUE 7: amount charged equals the order total ==");
        RecordingPayment recorder = new RecordingPayment();
        Order order = sampleOrder();
        order.checkout(recorder);
        check(recorder.calls == 1, "rail was called exactly once");
        check(Math.abs(recorder.lastAmount - order.calculateTotal().doubleValue()) < 0.0000001,
                "amount passed to payment == order.calculateTotal()");

        System.out.println("== ISSUE 2: rail rules + ISSUE 3: failure never says complete ==");
        WalletPayment poorWallet = new WalletPayment(100);
        String failedOut = capture(() -> sampleOrder().checkout(poorWallet));
        System.out.print(failedOut);
        check(poorWallet.getBalance() == 100, "declined wallet keeps its balance");
        check(failedOut.contains("Declined"), "console says Declined");
        check(failedOut.contains("NOT completed"), "console says order NOT completed");
        check(!failedOut.contains("Order 1001 completed"), "console never says the order completed on a failure");

        String successOut = capture(() -> sampleOrder().checkout(new WalletPayment(1000)));
        check(successOut.contains("completed: R174.97 paid via WALLET"),
                "success line ties order, amount and rail together");

        check(!new BuyNowPayLaterPayment().processPayment(2000.01), "BNPL declines above its limit");
        check(!new EftPayment(" ").processPayment(100), "EFT declines without a reference");
        check(!new CardPayment(500).processPayment(500.01), "card declines above its limit");

        System.out.println("== ISSUE 4: shared amount rule, written once ==");
        RecordingPayment untouched = new RecordingPayment();
        check(!new PaymentProcessor().processPayment(untouched, 0), "R0 rejected by the processor");
        check(!new PaymentProcessor().processPayment(untouched, -5), "negative rejected by the processor");
        check(untouched.calls == 0, "the rail was never even called");

        System.out.println("== Guard clauses: null rail, empty order ==");
        check(!sampleOrder().checkout(null), "null payment method does not crash");
        check(!new Order(2).checkout(new RecordingPayment()), "empty order is not charged");

        System.out.println("== ISSUE 5: money type ==");
        check(0.1 + 0.2 != 0.3, "double: 0.1 + 0.2 is NOT 0.3 (" + (0.1 + 0.2) + ")");
        check(Money.rands("0.1").add(Money.rands("0.2")).compareTo(Money.rands("0.3")) == 0,
                "BigDecimal: 0.1 + 0.2 is exactly 0.3");
        check(Money.toContractDouble(Money.rands("174.97")) == 174.97,
                "a normal Rand amount survives the double boundary");

        System.out.println("== BRIDGE: Capstone 4 preview, rail chosen at runtime ==");
        SmartCheckout smart = new SmartCheckout(new AmountBasedSelection(Money.rands("500")));
        List<PaymentMethod> pool = Arrays.asList(
                new WalletPayment(10), new EftPayment("INV-1002"));
        // Wallet is preferred for a small order but only holds R10, so the
        // fallback loop moves on to EFT.
        check(smart.pay(sampleOrder(), pool), "wallet declined, fallback to EFT succeeded");

        System.out.println();
        System.out.println(checks + " checks passed.");
    }

    /** Two-decimal Rand text, e.g. "174.97". Locale fixed so it prints the same everywhere. */
    private static String rands(double amount) {
        return String.format(Locale.ENGLISH, "%.2f", amount);
    }
}
