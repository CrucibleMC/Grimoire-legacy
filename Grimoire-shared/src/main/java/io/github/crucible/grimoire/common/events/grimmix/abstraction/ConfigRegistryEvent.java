package io.github.crucible.grimoire.common.events.grimmix.abstraction;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.core.MixinConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class ConfigRegistryEvent extends LifecycleEvent {
    protected final List<String> configurationCandidates;

    public ConfigRegistryEvent(LoadingStage stage, IGrimmix owner, List<String> configurationCandidates) {
        super(stage, owner);

        this.configurationCandidates = configurationCandidates;
    }

    public IMixinConfiguration registerConfiguration(String path) {
        return new MixinConfiguration(this.owner, this.stage.getAssociatedConfigurationType(), path, false);
    }

    public List<IMixinConfiguration> registerConfigurations(String... paths) {
        List<IMixinConfiguration> list = new ArrayList<>();

        for (String path : paths) {
            list.add(this.registerConfiguration(path));
        }

        return list;
    }

    public List<String> getConfigurationCandidates() {
        return this.configurationCandidates;
    }

    public List<IMixinConfiguration> registerConfigurationCandidates() {
        return this.registerConfigurationCandidates(cadidate -> true);
    }

    public List<IMixinConfiguration> registerConfigurationCandidates(Predicate<String> withPredicate) {
        List<IMixinConfiguration> list = new ArrayList<IMixinConfiguration>();

        for (String candidate : this.getConfigurationCandidates()) {
            if (withPredicate.test(candidate)) {
                list.add(this.registerConfiguration(candidate));
            }
        }

        return list;
    }

}
