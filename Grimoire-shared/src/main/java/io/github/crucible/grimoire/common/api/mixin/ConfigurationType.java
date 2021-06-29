package io.github.crucible.grimoire.common.api.mixin;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;

/**
 * Because Forge mod loading system begins mod discovery much later than coremods
 * are loaded, attempting to load mixin configurations designed to target other
 * mods at the time of coremod loading will fail, since no mods are discovered and
 * added to classpath at that time yet, and hereby classes from them do not yet exist
 * in runtime. This creates a neccessity to split all registered mixin configurations
 * in two groups - those that target Minecraft/Forge itself, and hereby should be
 * applied as early as possible, and those that target other mods, which hereby
 * should be applied at the time classess from those mods are actually accessible.
 *
 * @author Aizistral
 */

public enum ConfigurationType {
    /**
     * Signifies this configuration as one targeting Forge/Minecraft core.<br/>
     * Core configurations are applied immediately after Grimoire has finished
     * dispatching {@link ICoreLoadEvent} instances to all currently valid controllers;
     * which in turn happens immediately after {@link IValidationEvent}s and
     * {@link IConfigBuildingEvent}s have passed.
     */
    CORE,

    /**
     * Signifies this configuration as one targeting another mod.<br/>
     * Mod configurations are applied immediately after Grimoire has finished
     * dispatching {@link IModLoadEvent} instances to all currently valid controllers;
     * which in turn happens right before Forge is about to begin constructing mod instances.
     * This has to be delayed until then, since mods are not added to classpath
     * at the time of coremod loading, when {@link #CORE} configurations are loaded.<br/>
     */
    MOD;

    private ConfigurationType() {
        // NO-OP
    }

    /**
     * @return Loading stage associated with this configuration type.
     * @see LoadingStage
     */
    public LoadingStage getAssociatedLoadingStage() {
        if (this == CORE)
            return LoadingStage.CORELOAD;
        else if (this == MOD)
            return LoadingStage.MODLOAD;
        else
            return null;
    }

}