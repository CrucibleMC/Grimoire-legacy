package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.core.MixinConfiguration;
import io.github.crucible.grimoire.common.events.grimmix.CoreLoadEvent;

import java.util.Collections;
import java.util.List;

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
