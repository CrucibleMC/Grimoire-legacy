package io.github.crucible.grimoire.common.api.grimmix.events;

import io.github.crucible.grimoire.common.api.events.ICancelable;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;
import io.github.crucible.grimoire.common.core.GrimmixLoader;

/**
 * Controller validation event.<br/>
 * Dispatched after Grimoire finished scanning for controllers and constructing their instances.
 * If this event is canceled, Grimoire will drop controller from the list of loaded ones
 * and will make no further attempts to interact with it through lifecycle events.
 *
 * @author Aizistral
 */

public class GrimmixValidationEvent extends GrimmixLifecycleEvent implements IValidationEvent, ICancelable {
    protected boolean isCancelled;

    public GrimmixValidationEvent() {
        super(GrimmixLoader.INSTANCE.getActiveContainer());
    }

    @Override
    public void invalidate() {
        this.setCanceled(true);
    }

    @Override
    public boolean isValid() {
        return !this.isCanceled();
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }

    @Override
    public boolean isCanceled() {
        return isCancelled;
    }

    @Override
    public void setCanceled(boolean state) {
        isCancelled = state;
    }
}
