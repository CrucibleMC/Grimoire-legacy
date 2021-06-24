package io.github.crucible.grimoire.common.events.grimmix;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfigurationBuilder;
import io.github.crucible.grimoire.common.core.runtimeconfig.MixinConfigBuilder;
import io.github.crucible.grimoire.common.events.grimmix.abstraction.LifecycleEvent;

public class ConfigBuildingEvent extends LifecycleEvent implements IConfigBuildingEvent {

    public ConfigBuildingEvent(IGrimmix owner) {
        super(LoadingStage.MIXIN_CONFIG_BUILDING, owner);
    }

    @Override
    public IMixinConfigurationBuilder createBuilder(String configurationPath) {
        return new MixinConfigBuilder(this.owner, configurationPath);
    }

}
