package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfigurationBuilder;

public interface IConfigBuildingEvent extends ILifecycleEvent {

    public IMixinConfigurationBuilder createBuilder(String configurationPath);

}
