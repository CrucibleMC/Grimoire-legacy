package io.github.crucible.grimoire.api.grimmix.lifecycle.abstraction;

import io.github.crucible.grimoire.api.configurations.IMixinConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface ILifecycleRegistryEvent extends ILifecycleEvent {

    IMixinConfiguration registerConfiguration(String path);

    default List<IMixinConfiguration> registerConfigurations(String... paths) {
        List<IMixinConfiguration> list = new ArrayList<>();

        for (String path : paths) {
            list.add(this.registerConfiguration(path));
        }

        return list;
    }

    List<String> getConfigurationCandidates();

    default List<IMixinConfiguration> registerConfigurationCandidates() {
        return this.registerConfigurationCandidates(cadidate -> true);
    }

    default List<IMixinConfiguration> registerConfigurationCandidates(Predicate<String> withPredicate) {
        List<IMixinConfiguration> list = new ArrayList<IMixinConfiguration>();

        for (String candidate : this.getConfigurationCandidates()) {
            if (withPredicate.test(candidate)) {
                list.add(this.registerConfiguration(candidate));
            }
        }

        return list;
    }

}
