package io.github.crucible.grimoire.common.events.grimmix;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.events.grimmix.abstraction.LifecycleEvent;

public class FinishLoadEvent extends LifecycleEvent implements IFinishLoadEvent {

    public FinishLoadEvent(IGrimmix owner) {
        super(LoadingStage.FINAL, owner);
    }

}
