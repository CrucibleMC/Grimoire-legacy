package io.github.crucible.grimoire.common.test;

import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;

@Grimmix(modid = "grimoire", name = "Grimoire", version = "1.0.0")
public class GrimoireController extends GrimmixController {

    @Override
    public void validateController(IValidationEvent event) {
        // NO-OP
    }

    @Override
    public void coreLoad(ICoreLoadEvent event) {
        // NO-OP
    }

    @Override
    public void modLoad(IModLoadEvent event) {
        // NO-OP
    }

    @Override
    public void finish(IFinishLoadEvent event) {
        // NO-OP
    }

}
