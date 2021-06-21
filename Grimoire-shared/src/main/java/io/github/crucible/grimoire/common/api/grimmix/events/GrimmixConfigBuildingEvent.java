package io.github.crucible.grimoire.common.api.grimmix.events;

import io.github.crucible.grimoire.common.api.configurations.building.IMixinConfigBuilder;
import io.github.crucible.grimoire.common.api.events.ICancelable;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IMixinConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.core.MixinConfigBuilder;

public class GrimmixConfigBuildingEvent extends GrimmixLifecycleEvent implements ICancelable, IMixinConfigBuildingEvent {
    protected boolean isCancelled;

    public GrimmixConfigBuildingEvent() {
        super(GrimmixLoader.INSTANCE.getActiveContainer());
    }

    @Override
    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public boolean isCanceled() {
        return this.isCancelled;
    }

    @Override
    public void setCanceled(boolean state) {
        this.isCancelled = state;
    }

    @Override
    public IMixinConfigBuilder createBuilder(String configurationPath) {
        return new MixinConfigBuilder(this.grimmix, configurationPath);
    }

    @Override
    public LoadingStage getLoadingStage() {
        return LoadingStage.MIXIN_CONFIG_BUILDING;
    }
}