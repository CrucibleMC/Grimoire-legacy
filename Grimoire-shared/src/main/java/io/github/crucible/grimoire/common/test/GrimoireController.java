package io.github.crucible.grimoire.common.test;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IMixinConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;

@Grimmix(modid = "grimoire", name = "Grimoire", version = "1.0.0")
public class GrimoireController extends GrimmixController {

    @Override
    public void validateController(IValidationEvent event) {
        // NO-OP
    }

    @Override
    public void buildMixinConfigs(IMixinConfigBuildingEvent event) {
        event.createBuilder("omg/randommixins.json")
        .mixinPackage("io.github.crucible.grimoire.common.test.mixins")
        .configurationType(ConfigurationType.CORE)
        .clientMixins("client.*")
        .mixinPriority(1001)
        .required(true)
        .build();
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
