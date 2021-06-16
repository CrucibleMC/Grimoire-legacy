package io.github.crucible.omniconfig.api.lib;

import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

public class Either<A, B> {
    private final A a;
    private final B b;

    private Either(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public boolean isA() {
        return this.a != null;
    }

    public boolean isB() {
        return this.b != null;
    }

    public boolean ifA(Consumer<A> consumer) {
        if (this.isA()) {
            consumer.accept(this.a);
        }

        return this.isA();
    }

    public boolean ifB(Consumer<B> consumer) {
        if (this.isB()) {
            consumer.accept(this.b);
        }

        return this.isB();
    }

    public void execute(Consumer<A> caseA, Consumer<B> caseB) {
        if (this.ifA(caseA)) {
            // NO-OP
        } else {
            caseB.accept(this.b);
        }
    }

    public A getA() {
        return Preconditions.checkNotNull(this.a);
    }

    public B getB() {
        return Preconditions.checkNotNull(this.b);
    }

    public static <A, B> Either<A, B> fromA(@NotNull A a) {
        return new Either<A, B>(Objects.requireNonNull(a), (B)null);
    }

    public static <A, B> Either<A, B> fromB(@NotNull B b) {
        return new Either<A, B>((A)null, Objects.requireNonNull(b));
    }
}
