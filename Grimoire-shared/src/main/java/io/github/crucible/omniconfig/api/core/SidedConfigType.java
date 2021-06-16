package io.github.crucible.omniconfig.api.core;

import org.jetbrains.annotations.Nullable;

import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.grimoire.common.api.lib.Side;

public enum SidedConfigType {
    CLIENT,
    SERVER,
    COMMON;

    public boolean isSided() {
        return this != COMMON;
    }

    @Nullable
    public Side getSide() {
        if (this == CLIENT)
            return Side.CLIENT;
        else if (this == SERVER)
            return Side.DEDICATED_SERVER;
        else
            return null;
    }

    public void executeSided(Runnable run) {
        if (this.isSided()) {
            GrimoireInternals.executeInEnvironment(this.getSide(), () -> { return run; });
        } else {
            run.run();
        }
    }

}