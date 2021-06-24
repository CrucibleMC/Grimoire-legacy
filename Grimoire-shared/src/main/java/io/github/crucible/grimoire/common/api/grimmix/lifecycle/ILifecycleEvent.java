package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;

public abstract interface ILifecycleEvent {

    public IGrimmix getOwner();

    public LoadingStage getLoadingStage();

}
