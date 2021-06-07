package io.github.crucible.grimoire.common;

import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.crucible.grimoire.common.api.lib.Side;
import io.github.crucible.grimoire.common.core.GrimoireCore;

public class GrimoireInternals {

    public static void executeInEnvironment(Side side, Supplier<Runnable> supplier) {
        if (side == getEnvironment()) {
            supplier.get().run();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void ifInstance(Object obj, Class<T> ofClass, Consumer<T> consumer) {
        if (ofClass.isAssignableFrom(obj.getClass())) {
            consumer.accept((T) obj);
        }
    }

    public static Side getEnvironment() {
        return GrimoireCore.INSTANCE.getEnvironment();
    }

}