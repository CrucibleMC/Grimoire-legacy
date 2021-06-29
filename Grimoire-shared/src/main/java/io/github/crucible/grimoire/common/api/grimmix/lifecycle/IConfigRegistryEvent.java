package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import java.util.List;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixins;

import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;

/**
 * General interface reflecting modes of interaction for all lifecycle
 * events that allow to register native mixin configurations.
 *
 * @author Aizistral
 */

public abstract interface IConfigRegistryEvent extends ILifecycleEvent {

    /**
     * Register mixin configuration .json, with {@link ConfigurationType}
     * appropriate to this registry event.
     *
     * @param path Path to your configuration. Identical to what you would
     * pass into {@link Mixins#addConfiguration(String)} if you weren't using
     * Grimoire.
     * @return {@link IMixinConfiguration} instance.
     * @see IMixinConfiguration.
     */
    public IMixinConfiguration registerConfiguration(String path);

    /**
     * Does the same work as {@link #registerConfiguration(String)}, but for
     * all configuration paths passed in arguments.
     *
     * @param paths Array of configurations you desire to register.
     * @return List of all {@link IMixinConfiguration} instances created.
     * @see IMixinConfiguration
     */
    public List<IMixinConfiguration> registerConfigurations(String... paths);

    /**
     * During annotation analysis phase, Grimoire does automatically discover
     * all .json's within analyzed .jar file. This list exposes all configurations
     * that were located within the same file as your Grimmix controller.
     *
     * @return List of .json configurations found within your .jar file.
     */
    public List<String> getConfigurationCandidates();

    /**
     * Automatically register all configuration candidates provided by
     * {@link #getConfigurationCandidates()}.
     *
     * @return List of {@link IMixinConfiguration} instances created.
     */
    public List<IMixinConfiguration> registerConfigurationCandidates();

    /**
     * Automatically register all configuration candidates provided by
     * {@link #getConfigurationCandidates()} that satisfy supplied predicate.
     *
     * @param withPredicate Predicate configuration candidates must satisfy.
     * @return List of {@link IMixinConfiguration} instances created.
     */
    public List<IMixinConfiguration> registerConfigurationCandidates(Predicate<String> withPredicate);

}
