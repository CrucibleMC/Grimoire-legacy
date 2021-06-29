package io.github.crucible.grimoire.common.api.integration;

import io.github.crucible.grimoire.common.api.GrimoireAPI;

/**
 * Serves as a version-independent way to contain and handle safe optional
 * integrations with other mods. The philosophy behind this goes as following:
 * if mod we are trying to integrate with is present at runtime, instantiate real
 * integration handler that will proxy calls to that mod, otherwise instantiate
 * dummy integration handler in which methods will perform no logic and return some
 * dummy values. Such system allows to safely refer to classes and methods from other mod
 * without your own mod dying in hellfire of exceptions if that other mod is not present
 * at runtime.<br/><br/>
 *
 * This approach have proven an effective strategy for integrating with mods like
 * EventHelper, which can only exist in an environment of dedicated server that supports
 * Bukkit API. See version-dependent integrations for more info and as an example of
 * actual implementation.
 *
 * @param <T> Type of integration this container handles.
 * @author Aizistral
 */

public abstract class ModIntegrationContainer<T extends IModIntegration> {

    /**
     * Current integration instance, real or dummy.
     */
    protected final T integration;

    /**
     * {@link IModIntegrationRegistry} will construct this at the time
     * of <code>FMLPreInitializationEvent</code><br/>
     * About good time to check whether we need real or dummy integration.
     */
    public ModIntegrationContainer() {
        T integration = null;
        if (GrimoireAPI.isModLoaded(this.getModID())) {
            integration = this.createRealIntegration();
        } else {
            integration = this.createDummyIntegration();
        }

        this.integration = integration;
    }

    /**
     * @return Current integration instance, real or dummy.
     */
    public T getIntegration() {
        return this.integration;
    }

    /**
     * @return Real integration, will be called in case mod with ID
     * supplied by {@link #getModID()} is present at runtime.
     */
    protected abstract T createRealIntegration();

    /**
     * @return Dummy integration, will be called in case mod with ID
     * supplied by {@link #getModID()} is not present at runtime.
     */
    protected abstract T createDummyIntegration();

    /**
     * @return Interface representation of your integration, which in
     * turn must extend {@link IModIntegration} interface.
     */
    public abstract Class<T> getIntegrationClass();

    /**
     * @return ID of mod this integration targets.
     */
    public abstract String getModID();

    public String getName() {
        return "Integration[" + this.getModID() + "]";
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
