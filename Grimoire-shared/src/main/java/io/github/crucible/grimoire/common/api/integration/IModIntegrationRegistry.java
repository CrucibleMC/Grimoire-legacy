package io.github.crucible.grimoire.common.api.integration;

import java.util.List;

import org.jetbrains.annotations.Nullable;

/**
 * Stores and handles all {@link IModIntegration}s and their appropriate
 * {@link ModIntegrationContainer}s.
 *
 * @author Aizistral
 */

public interface IModIntegrationRegistry {

    /**
     * Register container that operates some form of {@link IModIntegration}.
     * It will be constructed at the time of <code>FMLPreInitializationEvent</code>.
     *
     * @param containerClass Container class.
     */
    public void registerIntegration(Class<? extends ModIntegrationContainer<?>> containerClass);

    /**
     * Find mod integration instance that matches supplied class of its
     * interface representation.
     *
     * @param integrationClass Class of interface representation of desired
     * integration.
     * @return Integration instance if exists, null if does not.
     */
    @Nullable
    public <T extends IModIntegration> T getIntegration(Class<T> integrationClass);

    /**
     * @return List of all existing mod integrations. Unmodifiable.
     */
    public List<? extends IModIntegration> getAllIntegrations();

    /**
     * @return True if <code>FMLPreInitializationEvent</code> have already passed
     * and integration containers were constructed, false if that is yet to happen.
     * If containers are registered after <code>FMLPreInitializationEvent</code>,
     * they will be constructed immediately.
     */
    public boolean isInitialized();

}
