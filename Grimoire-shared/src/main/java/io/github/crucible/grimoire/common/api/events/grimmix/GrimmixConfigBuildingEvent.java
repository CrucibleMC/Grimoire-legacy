package io.github.crucible.grimoire.common.api.events.grimmix;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfigurationBuilder;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.core.MixinConfigBuilder;
import io.github.crucible.grimoire.common.events.grimmix.ConfigBuildingEvent;

public class GrimmixConfigBuildingEvent extends GrimmixLifecycleEvent<ConfigBuildingEvent> {

    public GrimmixConfigBuildingEvent(ConfigBuildingEvent event) {
        super(event);
    }
    
}