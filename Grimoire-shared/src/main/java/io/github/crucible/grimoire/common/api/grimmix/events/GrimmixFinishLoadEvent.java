package io.github.crucible.grimoire.common.api.grimmix.events;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.common.core.GrimmixLoader;

/**
 * Finalize Grimmix loading.<br/>
 * Dispatched after Grimmixes has sucessfully passed every previous lifecycle event.
 *
 * @author Aizistral
 */

public class GrimmixFinishLoadEvent extends GrimmixLifecycleEvent implements IFinishLoadEvent {
    public GrimmixFinishLoadEvent() {
        super(GrimmixLoader.INSTANCE.getActiveContainer());
    }
}