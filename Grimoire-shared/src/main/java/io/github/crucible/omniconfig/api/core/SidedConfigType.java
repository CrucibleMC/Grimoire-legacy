package io.github.crucible.omniconfig.api.core;

import org.jetbrains.annotations.Nullable;

import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.grimoire.common.api.lib.Environment;

public enum SidedConfigType {
    CLIENT,
    SERVER,
    COMMON;

    public boolean isSided() {
        return this != COMMON;
    }

    @Nullable
    public Environment getSide() {
        if (this == CLIENT)
            return Environment.CLIENT;
        else if (this == SERVER)
            return Environment.DEDICATED_SERVER;
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