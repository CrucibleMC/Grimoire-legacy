package io.github.crucible.grimoire.api.grimmix.lifecycle;

import io.github.crucible.grimoire.api.grimmix.lifecycle.abstraction.ILifecycleRegistryEvent;

public interface IModLoadEvent extends ILifecycleRegistryEvent {

    @Override
    default LoadingStage getLoadingStage() {
        return LoadingStage.MODLOAD;
    }

}
