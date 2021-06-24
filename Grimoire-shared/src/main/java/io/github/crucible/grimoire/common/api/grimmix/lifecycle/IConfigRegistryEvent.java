package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import java.util.List;
import java.util.function.Predicate;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;

public abstract interface IConfigRegistryEvent extends ILifecycleEvent {

    public IMixinConfiguration registerConfiguration(String path);

    public List<IMixinConfiguration> registerConfigurations(String... paths);

    public List<String> getConfigurationCandidates();

    public List<IMixinConfiguration> registerConfigurationCandidates();

    public List<IMixinConfiguration> registerConfigurationCandidates(Predicate<String> withPredicate);

}
