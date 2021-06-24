package io.github.crucible.grimoire.common.api.mixin;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;

public enum ConfigurationType {
    /**
     * Signifies this configuration as one targeting FML/MC core.<br/>
     * Core configurations are applied immediately after Grimoire coremod loading.<br/>
     */
    CORE,

    /**
     * Signifies this configuration as one targeting another mod.<br/>
     * Mod configurations are applied later, since mods are not added to classpath
     * at the time of coremod loading when {@link #CORE} configurations are loaded.<br/>
     */
    MOD;

    private ConfigurationType() {
        // NO-OP
    }

    public LoadingStage getAssociatedLoadingStage() {
        if (this == CORE)
            return LoadingStage.CORELOAD;
        else if (this == MOD)
            return LoadingStage.MODLOAD;
        else
            return null;
    }

}