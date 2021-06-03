package io.github.crucible.grimoire.api.grimmix.lifecycle;

import io.github.crucible.grimoire.api.grimmix.lifecycle.abstraction.ILifecycleEvent;

public interface IValidationEvent extends ILifecycleEvent {

    @Override
    default LoadingStage getLoadingStage() {
        return LoadingStage.VALIDATION;
    }

    void invalidate();

    boolean isValid();

}
