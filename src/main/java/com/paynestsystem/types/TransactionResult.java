package com.paynestsystem.types;

/**
 * A type-safe result wrapper that holds either a <strong>success value</strong>
 * of type {@code T} or an <strong>error message</strong>.
 *
 * <p>This class demonstrates <strong>Java generics</strong>. The type parameter
 * {@code <T>} lets the same container work with any payload type while
 * remaining type-safe at compile time:</p>
 * <ul>
 *   <li>{@code TransactionResult<String>} — success holds a reference number</li>
 *   <li>{@code TransactionResult<Money>} — success holds a refund amount</li>
 * </ul>
 *
 * <p>Objects are created via <strong>static factory methods</strong>
 * ({@link #success(Object)} and {@link #failure(String)}) rather than
 * public constructors. This pattern makes the intent of each call site
 * clear and prevents invalid states.</p>
 *
 * @param <T> the type of the success payload
 */
public class TransactionResult<T> {

    private final T value;
    private final String errorMessage;
    private final boolean success;

    private TransactionResult(T value, String errorMessage, boolean success) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.success = success;
    }

    /**
     * Creates a successful result containing the given value.
     *
     * @param value the success payload
     * @param <T>   the type of the payload
     * @return a successful {@code TransactionResult}
     */
    public static <T> TransactionResult<T> success(T value) {
        return new TransactionResult<>(value, null, true);
    }

    /**
     * Creates a failed result with the given error message.
     *
     * @param errorMessage description of what went wrong
     * @param <T>          the type parameter (unused in failure, but preserves type compatibility)
     * @return a failed {@code TransactionResult}
     */
    public static <T> TransactionResult<T> failure(String errorMessage) {
        return new TransactionResult<>(null, errorMessage, false);
    }

    /**
     * @return {@code true} if this result represents a successful operation
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the success value.
     *
     * @return the payload
     * @throws IllegalStateException if this result is a failure
     */
    public T getValue() {
        if (!success) {
            throw new IllegalStateException("Cannot get value from a failed result");
        }
        return value;
    }

    /**
     * Returns the error message.
     *
     * @return the error description
     * @throws IllegalStateException if this result is a success
     */
    public String getErrorMessage() {
        if (success) {
            throw new IllegalStateException("Cannot get error message from a successful result");
        }
        return errorMessage;
    }
}
