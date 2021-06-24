package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.core.MixinConfiguration;
import io.github.crucible.grimoire.common.events.grimmix.ModLoadEvent;

import java.util.Collections;
import java.util.List;

/**
 * Dispatched at the time when Grimoire loads mod-targeting configurations.<br/>
 * This has to be delayed until after {@link cpw.mods.fml.common.discovery.ModDiscoverer} finished collecting
 * mods and adding their files to classpath.
 *
 * @author Aizistral
 */

public class GrimmixModLoadEvent extends GrimmixLifecycleEvent<ModLoadEvent> {

    public GrimmixModLoadEvent(ModLoadEvent event) {
        super(event);
    }

}
