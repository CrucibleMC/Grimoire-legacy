package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.events.grimmix.ModLoadEvent;

/**
 * This event is dispatched for every valid {@link GrimmixController}, before
 * that controller receives its instance of {@link IModLoadEvent}.<br><br>
 *
 * This event is {@link ICancelable}. If canceled, grimmix controller associated with
 * it will never receive {@link IModLoadEvent}.
 *
 * @author Aizistral
 * @see IModLoadEvent
 */

public class GrimmixModLoadEvent extends GrimmixLifecycleEvent<ModLoadEvent> {

    public GrimmixModLoadEvent(ModLoadEvent event) {
        super(event);
    }

}
