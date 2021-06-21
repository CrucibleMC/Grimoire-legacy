package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.configurations.building.IMixinConfigBuilder;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.abstraction.ILifecycleEvent;

public interface IMixinConfigBuildingEvent extends ILifecycleEvent {

    public IMixinConfigBuilder createBuilder(String configurationPath);

}
