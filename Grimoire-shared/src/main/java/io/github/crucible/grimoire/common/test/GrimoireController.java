package io.github.crucible.grimoire.common.test;

import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigRegistryEvent;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.common.events.grimmix.ConfigBuildingEvent;
import io.github.crucible.grimoire.common.events.grimmix.CoreLoadEvent;
import io.github.crucible.grimoire.common.events.grimmix.FinishLoadEvent;
import io.github.crucible.grimoire.common.events.grimmix.ModLoadEvent;
import io.github.crucible.grimoire.common.events.grimmix.ValidationEvent;

@Grimmix(id = "grimoire", name = "Grimoire", version = "1.0.0")
public class GrimoireController extends GrimmixController {

    public GrimoireController() {
        super();
    }

    @Override
    public void buildMixinConfigs(IConfigBuildingEvent event) {
        event.createBuilder("omg/randommixins.json")
        .mixinPackage("io.github.crucible.grimoire.common.test.mixins")
        .configurationType(ConfigurationType.CORE)
        .clientMixins("client.*")
        .mixinPriority(1001)
        .required(true)
        .build();
    }

}
