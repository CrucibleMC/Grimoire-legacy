package io.github.crucible.grimoire.mc1_12_2.integration.eventhelper;

import io.github.crucible.grimoire.common.api.integration.ModIntegrationContainer;
import io.github.crucible.grimoire.mc1_12_2.api.integration.eventhelper.IEventHelperIntegration;

public class EHIntegrationContainer extends ModIntegrationContainer<IEventHelperIntegration> {

    public EHIntegrationContainer() {
        super();
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
    public Class<IEventHelperIntegration> getIntegrationClass() {
        return IEventHelperIntegration.class;
    }

    @Override
    public String getModID() {
        return "eventhelper";
    }

}
