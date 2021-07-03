package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;

/**
 * Abstract form of lifecycle event which all lifecycle events implement.
 *
 * @author Aizistral
 */

public interface ILifecycleEvent {

    /**
     * @return {@link IGrimmix} representation of your Grimmix controller.
     * @see IGrimmix
     */
    IGrimmix getOwner();

    /**
     * @return Loading stage this event happens at.
     * @see LoadingStage
     */
    LoadingStage getLoadingStage();

}
