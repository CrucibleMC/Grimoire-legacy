package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;

/**
 * This event is designed for registering mixin configurations
 * that do target Minecraft/Forge specifically, but never other mods.
 * Distinction between the two <b>is important</b>, see {@link ConfigurationType}
 * for more details.
 *
 * @author Aizistral
 * @see ConfigurationType
 */
public interface ICoreLoadEvent extends IConfigRegistryEvent {

    // NO-OP

}
