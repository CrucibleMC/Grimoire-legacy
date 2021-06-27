package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.events.grimmix.CoreLoadEvent;

/**
 * Dispatched at the time when Grimoire loads core configurations.<br/>
 * That is, immediately after Grimmix validation events have passed.
 *
 * @author Aizistral
 */

public class GrimmixCoreLoadEvent extends GrimmixLifecycleEvent<CoreLoadEvent> {

    public GrimmixCoreLoadEvent(CoreLoadEvent event) {
        super(event);
    }

}
