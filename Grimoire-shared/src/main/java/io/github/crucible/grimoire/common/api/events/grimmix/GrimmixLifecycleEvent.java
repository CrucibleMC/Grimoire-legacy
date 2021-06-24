package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.eventbus.CoreEvent;
import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.events.grimmix.abstraction.LifecycleEvent;

/**
 * Generic Grimmix lifecycle event.
 *
 * @author Aizistral
 */
public abstract class GrimmixLifecycleEvent<T extends LifecycleEvent> extends GrimoireEvent implements ICancelable {
    protected final IGrimmix grimmix;
    protected final LoadingStage stage;
    protected final T event;

    public GrimmixLifecycleEvent(T event) {
        this.grimmix = event.getOwner();
        this.stage = event.getLoadingStage();
        this.event = event;
    }

    public IGrimmix getEventOwner() {
        return this.grimmix;
    }

    public LoadingStage getLoadingStage() {
        return this.stage;
    }

}
