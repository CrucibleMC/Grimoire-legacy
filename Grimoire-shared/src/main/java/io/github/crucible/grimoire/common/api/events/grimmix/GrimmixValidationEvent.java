package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;
import io.github.crucible.grimoire.common.events.grimmix.ValidationEvent;

/**
 * This event is dispatched for every valid {@link GrimmixController}, before
 * that controller receives its instance of {@link IValidationEvent}.<br/><br/>
 *
 * This event is {@link ICancelable}. If canceled, grimmix controller associated with
 * it will never receive {@link IValidationEvent}; furthermore, such controller will
 * be considered invalidated and dropped from the list of loaded controllers.
 *
 * @author Aizistral
 * @see IValidationEvent#invalidate()
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
