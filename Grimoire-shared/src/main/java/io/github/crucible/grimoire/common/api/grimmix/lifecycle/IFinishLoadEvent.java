package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.abstraction.ILifecycleEvent;

public interface IFinishLoadEvent extends ILifecycleEvent {

    @Override
    default LoadingStage getLoadingStage() {
        return LoadingStage.FINAL;
    }

}
