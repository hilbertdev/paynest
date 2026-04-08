package com.paynestsystem.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Central demonstration class for the Java Types foundation module.
 *
 * <p>Each static method explores a different aspect of the Java type system
 * using PayNest fintech examples. Call {@link #runAllDemos()} to execute
 * every demonstration in sequence.</p>
 */
public class TypesDemo {

    // ------------------------------------------------------------------ //
    //  1. Primitive Types                                                 //
    // ------------------------------------------------------------------ //

    /**
     * Demonstrates all eight Java primitive types using fintech-relevant
     * variable names and values.
     *
     * <ul>
     *   <li>{@code byte}  — 8-bit integer  (-128 to 127)</li>
     *   <li>{@code short} — 16-bit integer (-32 768 to 32 767)</li>
     *   <li>{@code int}   — 32-bit integer (~-2.1 billion to ~2.1 billion)</li>
     *   <li>{@code long}  — 64-bit integer</li>
     *   <li>{@code float} — 32-bit floating point</li>
     *   <li>{@code double}— 64-bit floating point</li>
     *   <li>{@code boolean}— true / false</li>
     *   <li>{@code char}  — single Unicode character</li>
     * </ul>
     */
    public static void demoPrimitiveTypes() {
        // Whole-number types (integers)
        byte dayOfMonth = 25;                     // payment due date
        short branchCode = 2510;                  // bank branch number
        int transactionCount = 1_048_576;         // monthly transactions processed
        long accountNumber = 1_234_567_890L;      // unique account identifier

        // Floating-point types
        float approximateRate = 18.5f;            // approximate ZAR/USD exchange rate
        double balanceInRands = 125_499.99;       // precise account balance

        // Other primitives
        boolean isVerified = true;                // KYC verification status
        char currencySymbol = 'R';                // South African Rand symbol

        System.out.println("  byte   dayOfMonth      = " + dayOfMonth);
        System.out.println("  short  branchCode       = " + branchCode);
        System.out.println("  int    transactionCount = " + transactionCount);
        System.out.println("  long   accountNumber    = " + accountNumber);
        System.out.println("  float  approximateRate  = " + approximateRate);
        System.out.println("  double balanceInRands   = " + balanceInRands);
        System.out.println("  boolean isVerified      = " + isVerified);
        System.out.println("  char   currencySymbol   = " + currencySymbol);
    }

    // ------------------------------------------------------------------ //
    //  2. Reference Types                                                 //
    // ------------------------------------------------------------------ //

    /**
     * Demonstrates reference types: {@code String} and arrays.
     *
     * <p>Unlike primitives (which hold a value directly), reference variables
     * hold a <em>reference</em> (pointer) to an object on the heap.</p>
     */
    public static void demoReferenceTypes() {
        // String — the most common reference type
        String merchantName = "PayNest Electronics";

        // Array — a fixed-size container of elements
        String[] supportedBanks = {"FNB", "Standard Bank", "Nedbank", "ABSA", "Capitec"};

        System.out.println("  Merchant: " + merchantName);
        System.out.print("  Supported banks:");
        for (String bank : supportedBanks) {
            System.out.print(" " + bank);
        }
        System.out.println();
    }

    // ------------------------------------------------------------------ //
    //  3. Type Casting                                                    //
    // ------------------------------------------------------------------ //

    /**
     * Demonstrates widening (implicit) and narrowing (explicit) type casts.
     *
     * <p><strong>Widening</strong> — converting a smaller type to a larger one
     * happens automatically and is always safe (no data loss).</p>
     *
     * <p><strong>Narrowing</strong> — converting a larger type to a smaller one
     * requires an explicit cast and may lose data (e.g. truncation of
     * decimals).</p>
     */
    public static void demoTypeCasting() {
        // --- Widening (automatic, safe) ---
        int cents = 12550;
        double rands = cents / 100.0;              // int → double
        System.out.println("  Widening: " + cents + " cents = R" + rands);

        int quantity = 5;
        long bigQuantity = quantity;                // int → long
        System.out.println("  Widening: int " + quantity + " → long " + bigQuantity);

        // --- Narrowing (explicit cast, potential data loss) ---
        double totalRands = 199.99;
        int truncatedRands = (int) totalRands;      // double → int (decimals lost!)
        System.out.println("  Narrowing: " + totalRands + " → (int) " + truncatedRands + "  [decimals lost]");

        long bigAmount = 1_000_000L;
        int smallAmount = (int) bigAmount;          // long → int (safe here, but risky for large values)
        System.out.println("  Narrowing: long " + bigAmount + " → (int) " + smallAmount);
    }

    // ------------------------------------------------------------------ //
    //  4. Wrapper Classes                                                 //
    // ------------------------------------------------------------------ //

    /**
     * Demonstrates wrapper classes — object equivalents of primitives.
     *
     * <p>Each primitive type has a corresponding wrapper class in
     * {@code java.lang}: {@code int → Integer}, {@code double → Double},
     * {@code boolean → Boolean}, etc. Wrappers are needed when an object
     * is required (e.g. in collections like {@code List<Integer>}).</p>
     */
    public static void demoWrapperClasses() {
        Integer transactionId = Integer.valueOf(1001);
        Double balance = Double.valueOf(5000.75);
        Boolean isActive = Boolean.TRUE;

        System.out.println("  Integer transactionId = " + transactionId);
        System.out.println("  Double  balance       = " + balance);
        System.out.println("  Boolean isActive      = " + isActive);

        // Parsing strings to numbers — common when reading user input
        int parsed = Integer.parseInt("42");
        double parsedAmount = Double.parseDouble("99.99");
        System.out.println("  Parsed int:    " + parsed);
        System.out.println("  Parsed double: " + parsedAmount);

        // Useful constants
        System.out.println("  Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println("  Integer.MIN_VALUE = " + Integer.MIN_VALUE);
    }

    // ------------------------------------------------------------------ //
    //  5. Autoboxing & Unboxing                                           //
    // ------------------------------------------------------------------ //

    /**
     * Demonstrates autoboxing (primitive → wrapper) and unboxing
     * (wrapper → primitive), which Java performs automatically.
     */
    public static void demoAutoboxing() {
        // Autoboxing: primitive int is automatically wrapped in Integer
        Integer boxed = 42;
        System.out.println("  Autoboxed: int 42 → Integer " + boxed);

        // Unboxing: Integer is automatically unwrapped to int
        int unboxed = boxed;
        System.out.println("  Unboxed:   Integer " + boxed + " → int " + unboxed);

        // Autoboxing in collections — you cannot store primitives in a List,
        // but Java autoboxes them for you
        List<Integer> amounts = new ArrayList<>();
        amounts.add(100);    // autoboxed: int 100 → Integer.valueOf(100)
        amounts.add(250);
        amounts.add(50);
        System.out.println("  List<Integer> amounts = " + amounts);

        // Warning: unboxing a null wrapper throws NullPointerException!
        // Integer nullValue = null;
        // int crash = nullValue;  // ← would throw NullPointerException at runtime
        System.out.println("  Note: unboxing null throws NullPointerException — be careful!");
    }

    // ------------------------------------------------------------------ //
    //  6. Type Inference (var)                                            //
    // ------------------------------------------------------------------ //

    /**
     * Demonstrates the {@code var} keyword (Java 10+) for local-variable
     * type inference.
     *
     * <p>The compiler infers the type from the right-hand side of the
     * assignment. The variable is still statically typed — {@code var}
     * is not dynamic typing.</p>
     */
    public static void demoTypeInference() {
        var merchantName = "PayNest Store";               // inferred as String
        var balance = 5000.75;                             // inferred as double
        var transactionCount = 42;                         // inferred as int
        var accounts = new ArrayList<String>();            // inferred as ArrayList<String>

        accounts.add("Savings");
        accounts.add("Cheque");

        System.out.println("  var merchantName     → " + ((Object) merchantName).getClass().getSimpleName() + " = " + merchantName);
        System.out.println("  var balance          → double = " + balance);
        System.out.println("  var transactionCount → int = " + transactionCount);
        System.out.println("  var accounts         → " + accounts.getClass().getSimpleName() + " = " + accounts);
    }

    // ------------------------------------------------------------------ //
    //  7. instanceof & Pattern Matching                                   //
    // ------------------------------------------------------------------ //

    /**
     * Demonstrates {@code instanceof} with pattern matching (Java 16+) and
     * exhaustive {@code switch} over a sealed type (Java 21).
     *
     * @param account the account to inspect
     */
    public static void demoInstanceOfPatternMatching(AccountType account) {
        // Traditional instanceof + cast (pre-Java 16):
        //   if (account instanceof SavingsAccount) {
        //       SavingsAccount sa = (SavingsAccount) account;
        //       // use sa
        //   }
        //
        // Pattern matching instanceof (Java 16+) — cast and variable in one step:
        if (account instanceof SavingsAccount sa) {
            System.out.println("  instanceof: SavingsAccount with interest "
                    + String.format(java.util.Locale.US, "%.1f%%", sa.getInterestRate() * 100));
        }

        // Exhaustive switch over sealed type (Java 21) — no default needed
        String summary = switch (account) {
            case SavingsAccount sa -> "Savings: earns " + sa.calculateAnnualInterest().formatted() + "/year";
            case ChequeAccount ca -> "Cheque: " + ca.availableFunds().formatted() + " available";
            case CreditAccount cr -> "Credit: " + cr.availableCredit().formatted() + " remaining";
        };
        System.out.println("  switch result: " + summary);
    }

    // ------------------------------------------------------------------ //
    //  8. Generics                                                        //
    // ------------------------------------------------------------------ //

    /**
     * Demonstrates generics using {@link TransactionResult} and a
     * simple generic utility method.
     */
    public static void demoGenerics() {
        // Same container, different content — all type-safe at compile time
        TransactionResult<String> approved = TransactionResult.success("REF-20240101-001");
        TransactionResult<Money> refund = TransactionResult.success(new Money(5000, CurrencyCode.ZAR));
        TransactionResult<String> declined = TransactionResult.failure("Insufficient funds");

        System.out.println("  Approved:  success=" + approved.isSuccess() + ", ref=" + approved.getValue());
        System.out.println("  Refund:    success=" + refund.isSuccess() + ", amount=" + refund.getValue().formatted());
        System.out.println("  Declined:  success=" + declined.isSuccess() + ", error=" + declined.getErrorMessage());

        // Generic utility method
        String primary = null;
        String fallback = "DEFAULT-REF";
        String result = firstNonNull(primary, fallback);
        System.out.println("  firstNonNull(null, \"DEFAULT-REF\") = " + result);
    }

    /**
     * Returns the first argument if it is non-null, otherwise the second.
     *
     * <p>This is a <strong>generic method</strong> — the type parameter
     * {@code <T>} is declared before the return type and lets this single
     * method work with any reference type.</p>
     *
     * @param first  the preferred value
     * @param second the fallback value
     * @param <T>    the type of both arguments
     * @return {@code first} if non-null, else {@code second}
     */
    public static <T> T firstNonNull(T first, T second) {
        return first != null ? first : second;
    }

    // ------------------------------------------------------------------ //
    //  9. Enums & Records                                                 //
    // ------------------------------------------------------------------ //

    /**
     * Demonstrates the {@link CurrencyCode} enum and {@link Money} record
     * that were defined as standalone types in this package.
     */
    public static void demoEnumsAndRecords() {
        // Enum with fields and methods
        CurrencyCode usd = CurrencyCode.USD;
        System.out.println("  Enum: " + usd.name()
                + " (" + usd.getDisplayName() + ", symbol=" + usd.getSymbol() + ")");
        System.out.println("  $100 USD in ZAR = R" + String.format(java.util.Locale.US, "%.2f", usd.convertToZar(100)));

        // Record — compact data carrier with auto-generated equals, hashCode, toString
        Money price = new Money(12550, CurrencyCode.ZAR);
        Money shipping = new Money(4500, CurrencyCode.ZAR);
        Money total = price.add(shipping);
        System.out.println("  Record: price=" + price.formatted()
                + " + shipping=" + shipping.formatted()
                + " = " + total.formatted());
        System.out.println("  Record toString: " + price);
        System.out.println("  Record equality: " + price.equals(new Money(12550, CurrencyCode.ZAR)));
    }

    // ------------------------------------------------------------------ //
    //  Run All                                                            //
    // ------------------------------------------------------------------ //

    /**
     * Runs every type demonstration in order, with section headers.
     * This is the single entry point called from {@code PayNestApplication}.
     */
    public static void runAllDemos() {
        System.out.println("==============================================");
        System.out.println("  Foundation Module: Java Types");
        System.out.println("==============================================");
        System.out.println();

        System.out.println("--- 1. Primitive Types ---");
        demoPrimitiveTypes();
        System.out.println();

        System.out.println("--- 2. Reference Types ---");
        demoReferenceTypes();
        System.out.println();

        System.out.println("--- 3. Type Casting ---");
        demoTypeCasting();
        System.out.println();

        System.out.println("--- 4. Wrapper Classes ---");
        demoWrapperClasses();
        System.out.println();

        System.out.println("--- 5. Autoboxing & Unboxing ---");
        demoAutoboxing();
        System.out.println();

        System.out.println("--- 6. Type Inference (var) ---");
        demoTypeInference();
        System.out.println();

        System.out.println("--- 7. Enums & Records ---");
        demoEnumsAndRecords();
        System.out.println();

        System.out.println("--- 8. Sealed Classes & Pattern Matching ---");
        Money savingsBalance = new Money(125_000, CurrencyCode.ZAR);
        Money chequeBalance = new Money(300_000, CurrencyCode.ZAR);
        Money overdraft = new Money(500_000, CurrencyCode.ZAR);
        Money creditBalance = new Money(200_000, CurrencyCode.ZAR);
        Money creditLimit = new Money(1_000_000, CurrencyCode.ZAR);

        AccountType savings = new SavingsAccount("Alice", savingsBalance, 0.055);
        AccountType cheque = new ChequeAccount("Bob", chequeBalance, overdraft);
        AccountType credit = new CreditAccount("Carol", creditBalance, creditLimit);

        System.out.println("  " + savings.describe());
        System.out.println("  " + cheque.describe());
        System.out.println("  " + credit.describe());
        System.out.println();
        demoInstanceOfPatternMatching(savings);
        demoInstanceOfPatternMatching(cheque);
        demoInstanceOfPatternMatching(credit);
        System.out.println();

        System.out.println("--- 9. Generics ---");
        demoGenerics();
        System.out.println();

        System.out.println("==============================================");
        System.out.println("  End of Foundation Module");
        System.out.println("==============================================");
        System.out.println();
    }
}
