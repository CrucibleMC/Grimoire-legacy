package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.events.grimmix.FinishLoadEvent;

/**
 * Finalize Grimmix loading.<br/>
 * Dispatched after Grimmixes has sucessfully passed every previous lifecycle event.
 *
 * @author Aizistral
 */

public class GrimmixFinishLoadEvent extends GrimmixLifecycleEvent<FinishLoadEvent> {

    public GrimmixFinishLoadEvent(FinishLoadEvent event) {
        super(event);
    }

}