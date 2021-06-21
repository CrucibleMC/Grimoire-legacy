package io.github.crucible.grimoire.mc1_12.integrations.eventhelper;

import io.github.crucible.grimoire.common.integrations.IModIntegrationContainer;

public class EHIntegrationContainer extends IModIntegrationContainer<IEventHelperIntegration> {

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
    protected Class<IEventHelperIntegration> getIntegrationClass() {
        return IEventHelperIntegration.class;
    }

    @Override
    public String getModID() {
        return "eventhelper";
    }

}
