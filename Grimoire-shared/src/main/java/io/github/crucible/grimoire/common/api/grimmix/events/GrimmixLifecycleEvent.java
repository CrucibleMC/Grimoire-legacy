package io.github.crucible.grimoire.common.api.grimmix.events;

import io.github.crucible.grimoire.common.api.events.CancelableEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.abstraction.ILifecycleEvent;

/**
 * Generic Grimmix lifecycle event.
 *
 * @author Aizistral
 */

public abstract class GrimmixLifecycleEvent extends CancelableEvent implements ILifecycleEvent {
    protected final IGrimmix grimmix;

    public GrimmixLifecycleEvent(IGrimmix grimmix) {
        this.grimmix = grimmix;
    }

    @Override
    public IGrimmix getEventOwner() {
        return this.grimmix;
    }
}
