package io.github.crucible.grimoire.mc1_7_10.integration.eventhelper;

import cpw.mods.fml.common.Loader;
import io.github.crucible.grimoire.common.integrations.ModIntegrationContainer;
import io.github.crucible.grimoire.mc1_7_10.api.integration.eventhelper.IEventHelperIntegration;

public class EHIntegrationContainer extends ModIntegrationContainer<IEventHelperIntegration> {

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
