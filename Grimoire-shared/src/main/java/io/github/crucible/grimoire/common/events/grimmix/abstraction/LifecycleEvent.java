package io.github.crucible.grimoire.common.events.grimmix.abstraction;

import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;

public abstract class LifecycleEvent extends GrimoireEvent {
    protected final LoadingStage stage;
    protected final IGrimmix owner;

    public LifecycleEvent(LoadingStage stage, IGrimmix owner) {
        this.stage = stage;
        this.owner = owner;
    }

    public IGrimmix getOwner() {
        return this.owner;
    }

    public LoadingStage getLoadingStage() {
        return this.stage;
    }

}
