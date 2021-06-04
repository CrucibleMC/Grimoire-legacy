package io.github.crucible.grimoire.common.api.grimmix.events;

import com.google.common.collect.ImmutableList;
import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.core.MixinConfiguration;

import java.util.List;

/**
 * Dispatched at the time when Grimoire loads core configurations.<br/>
 * That is, immediately after Grimmix validation events have passed.
 *
 * @author Aizistral
 */

public class GrimmixCoreLoadEvent extends GrimmixLifecycleEvent implements ICoreLoadEvent {
    private final List<String> configurationCandidates;

    public GrimmixCoreLoadEvent(List<String> configurationCandidates) {
        super(GrimmixLoader.INSTANCE.getActiveContainer());
        this.configurationCandidates = configurationCandidates;
    }

    @Override
    public IMixinConfiguration registerConfiguration(String path) {
        MixinConfiguration configuration = new MixinConfiguration(this.grimmix, IMixinConfiguration.ConfigurationType.CORE, path);
        return configuration;
    }

    @Override
    public List<String> getConfigurationCandidates() {
        return ImmutableList.copyOf(this.configurationCandidates);
    }
}
