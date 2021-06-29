package io.github.crucible.grimoire.common.api.events.configurations;

import java.util.Optional;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;

/**
 * This event is dispatched whenever Grimoire is about to load
 * every particular {@link IMixinConfiguration}.<br><br>
 *
 * This event is {@link ICancelable}. If canceled, configuration
 * will not be loaded, and will become invalid for subsequent
 * loading attempts. It will also be unregistered from the list
 * of unclaimed configurations if it didn't have associated
 * {@link IGrimmix} owner, or from the list of that owner's
 * configurations if it did.
 *
 * @author Aizistral
 */

public class MixinConfigLoadEvent extends GrimoireEvent implements ICancelable {
    private final IMixinConfiguration configuration;
    private final IGrimmix owner;

    public MixinConfigLoadEvent(IGrimmix owner, IMixinConfiguration configuration) {
        this.configuration = configuration;
        this.owner = owner;
    }

    /**
     * @return {@link IMixinConfiguration} associated with this event.
     */
    public IMixinConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * @return If {@link IMixinConfiguration} associated with this event
     * has an {@link IGrimmix} owner, returns that owner. Otherwise, an
     * empty {@link Optional} is returned.
     */
    public Optional<IGrimmix> getOwner() {
        return Optional.ofNullable(this.owner);
    }

}
