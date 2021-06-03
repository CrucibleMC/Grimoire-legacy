package io.github.crucible.grimoire.api.grimmix;

import io.github.crucible.grimoire.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.api.grimmix.lifecycle.IValidationEvent;

public abstract class GrimmixController {

    protected final GrimmixController instance;

    public GrimmixController() {
        this.instance = this;
    }

    public GrimmixController getInstance() {
        return this.instance;
    }

    public abstract void validateController(IValidationEvent event);

    public abstract void coreLoad(ICoreLoadEvent event);

    public abstract void modLoad(IModLoadEvent event);

    public abstract void finish(IFinishLoadEvent event);

}
