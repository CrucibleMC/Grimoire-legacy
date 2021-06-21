package io.github.crucible.grimoire.common.core;

import com.google.common.base.Preconditions;

public abstract class VersionHandler {
    private static VersionHandler instance = null;

    protected VersionHandler() {
        // NO-OP
    }

    public abstract boolean isModLoaded(String modid);

    protected static void setVersionHandler(VersionHandler handler) {
        Preconditions.checkArgument(instance == null, "Handler already set!");
        instance = handler;
    }

    public static VersionHandler instance() {
        return instance;
    }

}
