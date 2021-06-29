package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.events.grimmix.CoreLoadEvent;

/**
 * This event is dispatched for every valid {@link GrimmixController}, before
 * that controller receives its instance of {@link ICoreLoadEvent}.<br/><br/>
 *
 * This event is {@link ICancelable}. If canceled, grimmix controller associated with
 * it will never receive {@link ICoreLoadEvent}.
 *
 * @author Aizistral
 * @see ICoreLoadEvent
 */

public class GrimmixCoreLoadEvent extends GrimmixLifecycleEvent<CoreLoadEvent> {

    public GrimmixCoreLoadEvent(CoreLoadEvent event) {
        super(event);
    }

}
