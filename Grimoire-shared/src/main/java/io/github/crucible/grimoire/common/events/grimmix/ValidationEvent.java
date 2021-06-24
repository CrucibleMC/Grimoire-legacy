package io.github.crucible.grimoire.common.events.grimmix;

import io.github.crucible.grimoire.common.api.eventbus.ICancelable;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.events.grimmix.abstraction.LifecycleEvent;

public class ValidationEvent extends LifecycleEvent implements IValidationEvent, ICancelable {

    public ValidationEvent(IGrimmix owner) {
        super(LoadingStage.VALIDATION, owner);
    }

    @Override
    public void invalidate() {
        this.setCanceled(true);
    }

    @Override
    public boolean isValid() {
        return !this.isCanceled();
    }

}
