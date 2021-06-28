package io.github.crucible.omniconfig.api.lib;

import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

/**
 * Generic object that serves as a container for storing one of two
 * possible object types. It is assumed that any particular instance of
 * {@link Either} will only ever store either one or another object type of
 * two that are defined; never simultaneously both, and never none of them.
 *
 * @param <A> First object type.
 * @param <B> Second object type.
 *
 * @author Aizistral
 */

public class Either<A, B> {
    private final A a;
    private final B b;

    private Either(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     * @return True if this {@link Either} stores object of the first type.
     */
    public boolean isFirst() {
        return this.a != null;
    }

    /**
     * @return True if this {@link Either} stores object of the second type.
     */
    public boolean isSecond() {
        return this.b != null;
    }

    /**
     * If this {@link Either} is of first type, executes given {@link Consumer}
     * passing instance of that object to it.
     *
     * @param consumer Consumer to execute.
     * @return True if cosumer was actually executed, false otherwise.
     */
    public boolean ifFirst(Consumer<A> consumer) {
        if (this.isFirst()) {
            consumer.accept(this.a);
        }

        return this.isFirst();
    }

    /**
     * If this {@link Either} is of second type, executes given {@link Consumer}
     * passing instance of that object to it.
     *
     * @param consumer Consumer to execute.
     * @return True if cosumer was actually executed, false otherwise.
     */
    public boolean ifSecond(Consumer<B> consumer) {
        if (this.isSecond()) {
            consumer.accept(this.b);
        }

        return this.isSecond();
    }

    /**
     * If this {@link Either} is of first type, execute first consumer passed;
     * otherwise, execute second consumer.
     *
     * @param caseFirst Consumer to execute if this {@link Either} is of first type.
     * @param caseSecond Cosumer to execute if this {@link Either} is of second type.
     * @return True if cosumer was actually executed, false otherwise.
     */
    public void execute(Consumer<A> caseFirst, Consumer<B> caseSecond) {
        if (this.ifFirst(caseFirst)) {
            // NO-OP
        } else {
            caseSecond.accept(this.b);
        }
    }

    /**
     * @return Stored object of first type.
     * @throws NullPointerException If this either does not store
     * object of first type.
     */
    public A getFirst() throws NullPointerException {
        return Preconditions.checkNotNull(this.a);
    }

    /**
     * @return Stored object of second type.
     * @throws NullPointerException If this either does not store
     * object of second type.
     */
    public B getSecond() throws NullPointerException {
        return Preconditions.checkNotNull(this.b);
    }

    /**
     * Create new {@link Either} instance that stores object of first type.
     *
     * @param first Object instance, must not be null.
     * @return New {@link Either} of first type.
     */
    public static <A, B> Either<A, B> fromFirst(@NotNull A first) {
        return new Either<A, B>(Objects.requireNonNull(first), (B)null);
    }

    /**
     * Create new {@link Either} instance that stores object of second type.
     *
     * @param first Object instance, must not be null.
     * @return New {@link Either} of second type.
     */
    public static <A, B> Either<A, B> fromSecond(@NotNull B second) {
        return new Either<A, B>((A)null, Objects.requireNonNull(second));
    }
}
