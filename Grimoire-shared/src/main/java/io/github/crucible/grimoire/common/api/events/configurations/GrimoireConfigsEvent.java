package io.github.crucible.grimoire.common.api.events.configurations;

import java.util.Collections;
import java.util.List;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;

/**
 * Superclass for events that happen when Grimoire is about to do
 * something with currently registered {@link IMixinConfiguration}s.
 *
 * @author Aizistral
 */

public abstract class GrimoireConfigsEvent extends GrimoireEvent {
    protected final ConfigurationType type;
    protected final LoadingStage stage;
    protected final List<IMixinConfiguration> configurations;

    public GrimoireConfigsEvent(List<IMixinConfiguration> configurations, LoadingStage stage) {
        this.configurations = configurations;
        this.stage = stage;
        this.type = stage.getAssociatedConfigurationType();
    }

    /**
     * @return Loading stage this event is associated with.
     * @see LoadingStage
     */
    public LoadingStage getLoadingStage() {
        return this.stage;
    }

    /**
     * @return Configuration type this event is associated with.
     * @see ConfigurationType
     */
    public ConfigurationType getConfigurationsType() {
        return this.type;
    }

    /**
     * This event is dispatched when Grimoire is about to load full set
     * of mixin configurations that have {@link ConfigurationType} associated
     * with {@link LoadingStage} this event happens at.<br><br>
     *
     * This event is {@link ICancelable}. If canceled, none of configurations
     * listed in {@link #getPreparedConfigurations()} will be loaded.
     *
     * @author Aizistral
     */

    public static class Pre extends GrimoireConfigsEvent implements ICancelable {

        public Pre(List<IMixinConfiguration> configurations, LoadingStage stage) {
            super(configurations, stage);
        }

        /**
         * @return Modifiable list of all configurations that Grimore intends to
         * load once this event is finished dispatching.
         */
        public List<IMixinConfiguration> getPreparedConfigurations() {
            return super.configurations;
        }

        /**
         * Remove specified {@link IMixinConfiguration} from the list of
         * configurations Grimoire will load once event is finished dispatching,
         * if it won't end up canceled.
         *
         * @param configuration Configuration to remove.
         * @return True if it was in list of prepared configurations, false otherwise.
         */
        public boolean removeConfiguration(IMixinConfiguration configuration) {
            return super.configurations.remove(configuration);
        }

        /**
         * Add specified {@link IMixinConfiguration} to the list of
         * configurations Grimoire will load once event is finished dispatching,
         * if it won't end up canceled.
         *
         * @param configuration Configuration to add.
         */
        public void addConfiguration(IMixinConfiguration configuration) {
            super.configurations.add(configuration);
        }

    }

    /**
     * This event is dispatched after Grimoire have loaded full set
     * of mixin configurations that have {@link ConfigurationType} associated
     * with {@link LoadingStage} this event happens at.
     *
     * @author Aizistral
     */

    public static class Post extends GrimoireConfigsEvent {

        public Post(List<IMixinConfiguration> configurations, LoadingStage stage) {
            super(configurations, stage);
        }

        /**
         * @return Unmodifiable list of all configurations that were
         * loaded at this {@link LoadingStage}.
         */
        public List<IMixinConfiguration> getLoadedConfigurations() {
            return Collections.unmodifiableList(super.configurations);
        }

    }

}