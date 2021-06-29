package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.events.grimmix.FinishLoadEvent;

/**
 * This event is dispatched for every valid {@link GrimmixController}, before
 * that controller receives its instance of {@link IFinishLoadEvent}.<br><br>
 *
 * This event is {@link ICancelable}. If canceled, grimmix controller associated with
 * it will never receive {@link IFinishLoadEvent}.
 *
 * @author Aizistral
 * @see IFinishLoadEvent
 */

public class GrimmixFinishLoadEvent extends GrimmixLifecycleEvent<FinishLoadEvent> {

    public GrimmixFinishLoadEvent(FinishLoadEvent event) {
        super(event);
    }

}