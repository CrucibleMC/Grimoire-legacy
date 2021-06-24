package io.github.crucible.grimoire.common.events.grimmix;

import java.util.List;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.events.grimmix.abstraction.ConfigRegistryEvent;

public class CoreLoadEvent extends ConfigRegistryEvent implements ICoreLoadEvent {

    public CoreLoadEvent(IGrimmix owner, List<String> configurationCandidates) {
        super(LoadingStage.CORELOAD, owner, configurationCandidates);
    }

}
