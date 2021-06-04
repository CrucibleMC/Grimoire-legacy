package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.abstraction.ILifecycleRegistryEvent;

public interface ICoreLoadEvent extends ILifecycleRegistryEvent {

    @Override
    default LoadingStage getLoadingStage() {
        return LoadingStage.CORELOAD;
    }

}
