package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.events.grimmix.ValidationEvent;

/**
 * Controller validation event.<br/>
 * Dispatched after Grimoire finished scanning for controllers and constructing their instances.
 * If this event is canceled, Grimoire will drop controller from the list of loaded ones
 * and will make no further attempts to interact with it through lifecycle events.
 *
 * @author Aizistral
 */

public class GrimmixValidationEvent extends GrimmixLifecycleEvent<ValidationEvent> {

    public GrimmixValidationEvent(ValidationEvent event) {
        super(event);
    }

    public void invalidate() {
        this.setCanceled(true);
    }

    public boolean isValid() {
        return !this.isCanceled();
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.event.setCanceled(true);
        super.setCanceled(cancel);
    }

    @Override
    public boolean isCanceled() {
        return this.event.isCanceled() && super.isCanceled();
    }

}
