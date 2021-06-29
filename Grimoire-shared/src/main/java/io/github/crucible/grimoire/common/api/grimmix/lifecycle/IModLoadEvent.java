package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;

/**
 * This event is designed for registering mixin configurations that do
 * not target Minecraft/Forge, but instead are aimed at other mods.<br/>
 * Distinction between the two <b>is important</b>, see {@link ConfigurationType}
 * for more details.
 *
 * @author Aizistral
 * @see ConfigurationType
 */
public interface IModLoadEvent extends IConfigRegistryEvent {

    // NO-OP

}
