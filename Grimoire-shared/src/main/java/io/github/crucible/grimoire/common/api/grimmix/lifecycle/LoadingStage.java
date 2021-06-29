package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import org.jetbrains.annotations.Nullable;

import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixValidationEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfigurationBuilder;

/**
 * Represents an amount of loading stages in which Grimoire handles
 * grimmix loading lifecycle.
 *
 * @author Aizistral
 */

public enum LoadingStage {

    /**
     * Initial loading stage; grimmixes are not discovered yet,
     * nothing much going on here besided Grimoire handling its
     * own configuration and initialization.
     */
    PRE_CONSTRUCTION,

    /**
     * Grimmix construction stage; at this point annotation analysis
     * is wrapped up, all files containing grimmix controllers are
     * added to classpath, constructors are collected and have
     * respective {@link IGrimmix} containers, but are only just now
     * invoked.
     */
    CONSTRUCTION,

    /**
     * Grimmix validation stage; at this point all grimmixes receive
     * {@link IValidationEvent} instances, which is preceeded by
     * all {@link GrimoireAPI#EVENT_BUS} subscribers receiving
     * {@link GrimmixValidationEvent} instances.
     */
    VALIDATION,

    /**
     * Runtime mixin configurations building stage. This is when
     * grimmix controllers receive {@link IConfigBuildingEvent} and
     * are free to instantiate {@link IMixinConfigurationBuilder} instances
     * in order to build their mixin configurations at runtime instead of
     * shipping native configurations within their .jar.
     */
    MIXIN_CONFIG_BUILDING,

    /**
     * Stage for registering native mixin configurations of
     * {@link ConfigurationType#CORE}. Happens immediately after both
     * {@link #CONSTRUCTION} and {@link #VALIDATION} stages were wrapped
     * up.
     * @see ConfigurationType
     */
    CORELOAD,

    /**
     * Stage for registering native mixin configurations of
     * {@link ConfigurationType#MOD}. Happens before all modfiles are modfiles
     * are about to be added to classpath; mixin configurations registered
     * during this stage are applied immediately after that happends.
     * @see ConfigurationType
     */
    MODLOAD,

    /**
     * Last loading stage, where {@link IFinishLoadEvent} is dispatched.
     * Serves no purpose beyond notification, might be useful for performing
     * some cleanup procedures after all previous events.
     */
    FINAL;

    public LoadingStage getNextStage() {
        return LoadingStage.values()[this.ordinal() + 1];
    }

    /**
     * @param stage Another loading stage.
     * @return True if supplied {@link LoadingStage} is the next stage in
     * grimmix loading lifecycle after this one, false otherwise.
     */
    public boolean isNextStage(LoadingStage stage) {
        return this.ordinal() + 1 == stage.ordinal();
    }

    /**
     * @return True if this loading stage is associated with registering
     * certain {@link ConfigurationType}, false if not.
     */
    public boolean isConfigurationStage() {
        return this == CORELOAD || this == LoadingStage.MODLOAD;
    }

    /**
     * @return If this loading stage is associated with registering certain
     * {@link ConfigurationType}, that type is returned. Otherwise returns
     * null.
     */
    @Nullable
    public ConfigurationType getAssociatedConfigurationType() {
        if (this == CORELOAD)
            return ConfigurationType.CORE;
        else if (this == MODLOAD)
            return ConfigurationType.MOD;
        else
            return null;
    }

}