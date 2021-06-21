package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;

public enum LoadingStage {
    PRE_CONSTRUCTION,
    CONSTRUCTION,
    VALIDATION,
    MIXIN_CONFIG_BUILDING,
    CORELOAD,
    MODLOAD,
    FINAL;

    public LoadingStage getNextStage() {
        return LoadingStage.values()[this.ordinal() + 1];
    }

    public boolean isNextStage(LoadingStage stage) {
        return this.ordinal() + 1 == stage.ordinal();
    }

    public boolean isConfigurationStage() {
        return this == CORELOAD || this == LoadingStage.MODLOAD;
    }

    public IMixinConfiguration.ConfigurationType getAssociatedConfigurationType() {
        if (this == CORELOAD)
            return IMixinConfiguration.ConfigurationType.CORE;
        else if (this == MODLOAD)
            return IMixinConfiguration.ConfigurationType.MOD;
        else
            return null;
    }
}