package io.github.crucible.grimoire.common.api.events.configurations;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.eventbus.CoreEvent;
import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;

import java.util.Optional;

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
