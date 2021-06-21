package io.github.crucible.grimoire.common.integrations;

import io.github.crucible.grimoire.common.core.VersionHandler;

public abstract class IModIntegrationContainer<T extends IModIntegration> {
    protected final T integration;

    public IModIntegrationContainer() {
        T integration = null;
        if (VersionHandler.instance().isModLoaded(this.getModID())) {
            integration = this.createRealIntegration();
        } else {
            integration = this.createDummyIntegration();
        }

        this.integration = integration;
    }

    public T getIntegration() {
        return this.integration;
    }

    protected abstract T createRealIntegration();

    protected abstract T createDummyIntegration();

    protected abstract Class<T> getIntegrationClass();

    public abstract String getModID();

    public String getName() {
        return "Integration[" + this.getModID() + "]";
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
