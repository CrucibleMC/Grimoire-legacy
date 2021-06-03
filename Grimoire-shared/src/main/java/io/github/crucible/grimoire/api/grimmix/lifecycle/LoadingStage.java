package io.github.crucible.grimoire.api.grimmix.lifecycle;

import io.github.crucible.grimoire.api.configurations.IMixinConfiguration.ConfigurationType;

public enum LoadingStage {
    PRE_CONSTRUCTION, CONSTRUCTION, VALIDATION, CORELOAD, MODLOAD, FINAL;

    public LoadingStage getNextStage() {
        return LoadingStage.values()[this.ordinal() + 1];
    }

    public boolean isNextStage(LoadingStage stage) {
        return this.ordinal() + 1 == stage.ordinal();
    }

    public boolean isConfigurationStage() {
        return this == CORELOAD || this == LoadingStage.MODLOAD;
    }

    public ConfigurationType getAssociatedConfigurationType() {
        if (this == CORELOAD)
            return ConfigurationType.CORE;
        else if (this == MODLOAD)
            return ConfigurationType.MOD;
        else
            return null;
    }
}