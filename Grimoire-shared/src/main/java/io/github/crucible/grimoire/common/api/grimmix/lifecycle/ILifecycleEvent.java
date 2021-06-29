package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;

/**
 * Abstract form of lifecycle event which all lifecycle events implement.
 *
 * @author Aizistral
 */

public abstract interface ILifecycleEvent {

    /**
     * @return {@link IGrimmix} representation of your Grimmix controller.
     * @see IGrimmix
     */
    public IGrimmix getOwner();

    /**
     * @return Loading stage this event happens at.
     * @see LoadingStage
     */
    public LoadingStage getLoadingStage();

}
