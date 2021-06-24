package io.github.crucible.grimoire.common.api.integration;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public interface IModIntegrationRegistry {

    public void registerIntegration(Class<? extends ModIntegrationContainer<?>> integrationClass);

    @Nullable
    public <T extends IModIntegration> T getIntegration(Class<T> integrationClass);

    public List<? extends IModIntegration> getAllIntegrations();

    public boolean isInitialized();

}
