package com.paynestsystem.app;

import java.util.List;

/**
 * Live lecture demo: polymorphism via an interface and dynamic dispatch.
 *
 * <p>This is <em>not</em> Capstone 2. It is a small, self-contained illustration so
 * students can see one checkout path call three different rails without
 * {@code if}/{@code switch} on payment type. Capstone 2 applies the same idea to
 * real {@code Order} checkout.
 *
 * <p>Run:
 * <pre>{@code
 * mvn -q compile exec:java -Dexec.mainClass="com.paynestsystem.app.PolymorphismLiveDemo"
 * }</pre>
 */
public final class PolymorphismLiveDemo {

    private PolymorphismLiveDemo() {
    }

    /**
     * Shared contract every payment rail must keep.
     * Callers depend on this type — never on Card / EFT / Wallet directly.
     */
    public interface PaymentRail {
        /** Human-readable rail label for console output (e.g. {@code CARD}). */
        String getPaymentType();

        /**
         * Charge the given Rand amount on this rail.
         *
         * @param amount order total in Rand
         * @return {@code true} if the simulated charge succeeded
         */
        boolean charge(double amount);
    }

    /** Simulated card rail. */
    public static final class CardRail implements PaymentRail {
        @Override
        public String getPaymentType() {
            return "CARD";
        }

        @Override
        public boolean charge(double amount) {
            System.out.println("  [CardRail] Authorising card for R" + formatRand(amount));
            return true;
        }
    }

    /** Simulated EFT (bank transfer) rail. */
    public static final class EftRail implements PaymentRail {
        @Override
        public String getPaymentType() {
            return "EFT";
        }

        @Override
        public boolean charge(double amount) {
            System.out.println("  [EftRail] Initiating bank transfer for R" + formatRand(amount));
            return true;
        }
    }

    /** Simulated digital-wallet rail. */
    public static final class WalletRail implements PaymentRail {
        @Override
        public String getPaymentType() {
            return "WALLET";
        }

        @Override
        public boolean charge(double amount) {
            System.out.println("  [WalletRail] Debiting wallet for R" + formatRand(amount));
            return true;
        }
    }

    /**
     * One checkout path. Notice: no mention of Card, EFT, or Wallet.
     * At runtime the JVM picks the concrete {@link #charge} override (dynamic dispatch).
     */
    public static void checkout(PaymentRail rail, double amount) {
        System.out.println("Checkout via " + rail.getPaymentType()
                + " (runtime type: " + rail.getClass().getSimpleName() + ")");
        boolean ok = rail.charge(amount);
        System.out.println(ok ? "  -> SUCCESS" : "  -> FAILED");
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("=== Polymorphism live demo (dynamic dispatch) ===");
        System.out.println("Same checkout(...) method · three PaymentRail implementations");
        System.out.println();

        double orderTotal = 12_400.0;

        // Variable type = interface; object type = concrete rail.
        PaymentRail card = new CardRail();
        PaymentRail eft = new EftRail();
        PaymentRail wallet = new WalletRail();

        // One call site, three behaviours — swap the object, keep the method.
        checkout(card, orderTotal);
        checkout(eft, orderTotal);
        checkout(wallet, orderTotal);

        // Same idea with a collection of the interface type.
        System.out.println("--- Loop over List<PaymentRail> ---");
        List<PaymentRail> rails = List.of(new CardRail(), new EftRail(), new WalletRail());
        for (PaymentRail rail : rails) {
            checkout(rail, 500.0);
        }

        System.out.println("Takeaway: add a new rail class, pass it to checkout — do not edit checkout.");
    }

    private static String formatRand(double amount) {
        return String.format("%.0f", amount);
    }
}
