package io.github.crucible.grimoire.common.events.grimmix;

import java.util.List;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.events.grimmix.abstraction.ConfigRegistryEvent;

public class ModLoadEvent extends ConfigRegistryEvent implements IModLoadEvent {

    public ModLoadEvent(IGrimmix owner, List<String> configurationCandidates) {
        super(LoadingStage.MODLOAD, owner, configurationCandidates);
    }

}
