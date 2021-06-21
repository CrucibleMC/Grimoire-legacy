package io.github.crucible.grimoire.mc1_7_10.integrations.eventhelper;

import cpw.mods.fml.common.Loader;
import io.github.crucible.grimoire.common.integrations.IModIntegrationContainer;

public class EHIntegrationContainer extends IModIntegrationContainer<IEventHelperIntegration> {

    public EHIntegrationContainer() {
        super();
    }

    @Override
    public String getModID() {
        return "EventHelper";
    }

    @Override
    public String getName() {
        return "Integration[EventHelper]";
    }

    @Override
    protected IEventHelperIntegration createRealIntegration() {
        return new RealEHIntegration();
    }

    @Override
    protected IEventHelperIntegration createDummyIntegration() {
        return new DummyEHIntegration();
    }

    @Override
    protected Class<IEventHelperIntegration> getIntegrationClass() {
        return IEventHelperIntegration.class;
    }
}
