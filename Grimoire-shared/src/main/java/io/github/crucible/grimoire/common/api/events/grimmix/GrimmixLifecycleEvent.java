package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ILifecycleEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.events.grimmix.abstraction.LifecycleEvent;

/**
 * Generic Grimmix lifecycle event. Implementations of this event are
 * dispatched for each valid {@link GrimmixController}, before that
 * controller receives some form of {@link ILifecycleEvent}.<br/><br/>
 *
 * All implementations of this event are {@link ICancelable}. If canceled,
 * associated Grimmix will never receive particular instance of {@link ILifecycleEvent}
 * that specific implementation is designed to handle.
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

    /**
     * @return Get {@link IGrimmix} representation of Grimmix controller
     * associated with this event.
     * @see IGrimmix
     */
    public IGrimmix getEventOwner() {
        return this.grimmix;
    }

    /**
     * @return Loading stage at which this event happens.
     * @see LoadingStage
     */
    public LoadingStage getLoadingStage() {
        return this.stage;
    }

}
