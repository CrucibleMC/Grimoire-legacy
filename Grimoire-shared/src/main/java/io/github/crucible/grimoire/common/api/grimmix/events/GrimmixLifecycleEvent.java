package io.github.crucible.grimoire.common.api.grimmix.events;

import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.events.core.CoreEvent;
import io.github.crucible.grimoire.common.api.events.core.ICancelable;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.abstraction.ILifecycleEvent;

/**
 * Generic Grimmix lifecycle event.
 *
 * @author Aizistral
 */
public abstract class GrimmixLifecycleEvent extends GrimoireEvent implements ILifecycleEvent, ICancelable {
    protected final IGrimmix grimmix;

    public GrimmixLifecycleEvent(IGrimmix grimmix) {
        this.grimmix = grimmix;
    }

    @Override
    public IGrimmix getEventOwner() {
        return this.grimmix;
    }
}
