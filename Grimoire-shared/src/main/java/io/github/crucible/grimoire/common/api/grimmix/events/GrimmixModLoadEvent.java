package io.github.crucible.grimoire.common.api.grimmix.events;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.core.MixinConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * Dispatched at the time when Grimoire loads mod-targeting configurations.<br/>
 * This has to be delayed until after {@link cpw.mods.fml.common.discovery.ModDiscoverer} finished collecting
 * mods and adding their files to classpath.
 *
 * @author Aizistral
 */

public class GrimmixModLoadEvent extends GrimmixLifecycleEvent implements IModLoadEvent {
    private final List<String> configurationCandidates;

    public GrimmixModLoadEvent(List<String> configurationCandidates) {
        super(GrimmixLoader.INSTANCE.getActiveContainer());
        this.configurationCandidates = configurationCandidates;
    }

    @Override
    public IMixinConfiguration registerConfiguration(String path) {
        return new MixinConfiguration(this.grimmix, IMixinConfiguration.ConfigurationType.MOD, path, false);
    }

    @Override
    public List<String> getConfigurationCandidates() {
        return Collections.unmodifiableList(this.configurationCandidates);
    }
}
