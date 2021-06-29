package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.events.grimmix.ConfigBuildingEvent;

/**
 * This event is dispatched for every valid {@link GrimmixController}, before
 * that controller receives its instance of {@link IConfigBuildingEvent}.<br/><br/>
 *
 * This event is {@link ICancelable}. If canceled, grimmix controller associated with
 * it will never receive {@link IConfigBuildingEvent}.
 *
 * @author Aizistral
 * @see IConfigBuildingEvent
 */

public class GrimmixConfigBuildingEvent extends GrimmixLifecycleEvent<ConfigBuildingEvent> {

    public GrimmixConfigBuildingEvent(ConfigBuildingEvent event) {
        super(event);
    }

}