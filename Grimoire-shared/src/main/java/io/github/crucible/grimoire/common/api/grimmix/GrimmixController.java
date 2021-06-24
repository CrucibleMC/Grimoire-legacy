package io.github.crucible.grimoire.common.api.grimmix;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;

public abstract class GrimmixController {

    public GrimmixController() {
        // NO-OP
    }

    public void validateController(IValidationEvent event) {
        // NO-OP
    }

    public void buildMixinConfigs(IConfigBuildingEvent event) {
        // NO-OP
    }

    public void coreLoad(ICoreLoadEvent event) {
        // NO-OP
    }

    public void modLoad(IModLoadEvent event) {
        // NO-OP
    }

    public void finish(IFinishLoadEvent event) {
        // NO-OP
    }

}
