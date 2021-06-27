package io.github.crucible.grimoire.common.api.events.configurations;

import java.util.Optional;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;

public class MixinConfigLoadEvent extends GrimoireEvent implements ICancelable {
    private final IMixinConfiguration configuration;
    private final IGrimmix owner;

    public MixinConfigLoadEvent(IGrimmix owner, IMixinConfiguration configuration) {
        this.configuration = configuration;
        this.owner = owner;
    }

    public IMixinConfiguration getConfiguration() {
        return this.configuration;
    }

    public Optional<IGrimmix> getOwner() {
        return Optional.ofNullable(this.owner);
    }

}
