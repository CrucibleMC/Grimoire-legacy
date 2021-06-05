package io.github.crucible.grimoire.common;

import java.util.function.Supplier;

import io.github.crucible.grimoire.common.api.lib.Side;
import io.github.crucible.grimoire.common.core.GrimoireCore;

public class GrimoireInternals {

    public static void executeOnSide(Side side, Supplier<Runnable> supplier) {
        if (side == GrimoireCore.INSTANCE.getSide()) {
            supplier.get().run();
        }
    }

}