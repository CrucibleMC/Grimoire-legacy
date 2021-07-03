package io.github.crucible.grimoire.common.api.mixin;

import java.util.Optional;

import io.github.crucible.grimoire.common.api.events.configurations.MixinConfigLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;

/**
 * Representation for all mixin configurations registered through
 * Grimoire. Allows to retain some data about mixin configuration
 * and its current state after it was registered.
 *
 * @author Aizistral
 */

public interface IMixinConfiguration {

    /**
     * @return If this configuration is owned by a particular grimmix,
     * return its {@link IGrimmix} container. Otherwise and empty
     * {@link Optional} is returned.
     */
    Optional<IGrimmix> getOwner();

    /**
     * @return Path to this configuration in runtime classpath.
     */
    String getClasspath();

    /**
     * @return Type of this configuration.
     * @see ConfigurationType
     */
    ConfigurationType getConfigurationType();

    /**
     * @return True if this configuration was already loaded and applied.
     * False if it yet awaits to actually be loaded.
     */
    boolean isLoaded();

    /**
     * @return True if this configuration was built at runtime with the
     * helf of {@link IMixinConfigurationBuilder}; false if this is native
     * configuration shipped within mod file.
     */
    boolean isRuntimeGenerated();

    /**
     * @return True if this configuration could potentially load at this
     * point in time, but is not yet. For runtime-generated configurations
     * this will return false until actual .jar file containing them is
     * not created by Grimoire.
     */
    boolean canLoad();

    /**
     * @return False if this configuration was invalidated in
     * {@link MixinConfigLoadEvent} and dropped from overall registry of
     * configurations; true if not (or not yet).
     */
    boolean isValid();

    // public void load();

}