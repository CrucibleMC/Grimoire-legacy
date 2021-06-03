package io.github.crucible.grimoire.api.configurations.events;

import com.google.common.collect.ImmutableList;
import io.github.crucible.grimoire.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.api.configurations.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.api.events.GrimoireEvent;
import io.github.crucible.grimoire.api.events.ICancelable;
import io.github.crucible.grimoire.api.grimmix.lifecycle.LoadingStage;

import java.util.List;

public abstract class GrimoireConfigurationsEvent extends GrimoireEvent {
    protected final ConfigurationType type;
    protected final LoadingStage stage;
    protected final List<IMixinConfiguration> configurations;

    public GrimoireConfigurationsEvent(List<IMixinConfiguration> configurations, LoadingStage stage) {
        this.configurations = configurations;
        this.stage = stage;
        this.type = stage.getAssociatedConfigurationType();
    }

    public LoadingStage getLoadingStage() {
        return this.stage;
    }

    public ConfigurationType getConfigurationsType() {
        return this.type;
    }

    public static class Pre extends GrimoireConfigurationsEvent implements ICancelable {
        protected boolean isCancelled;

        public Pre(List<IMixinConfiguration> configurations, LoadingStage stage) {
            super(configurations, stage);
        }

        public List<IMixinConfiguration> getPreparedConfigurations() {
            return super.configurations;
        }

        public boolean removeConfiguration(IMixinConfiguration configuration) {
            return super.configurations.remove(configuration);
        }

        public void addConfiguration(IMixinConfiguration configuration) {
            super.configurations.add(configuration);
        }

        @Override
        public void cancel() {
            isCancelled = true;
        }

        @Override
        public boolean isCanceled() {
            return isCancelled;
        }

        @Override
        public void setCanceled(boolean state) {
            isCancelled = state;
        }
    }

    public static class Post extends GrimoireConfigurationsEvent {
        public Post(List<IMixinConfiguration> configurations, LoadingStage stage) {
            super(configurations, stage);
        }

        public List<IMixinConfiguration> getLoadedConfigurations() {
            return ImmutableList.copyOf(super.configurations);
        }
    }

}
