package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.eventbus.CoreEventHandler;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixValidationEvent;

/**
 * This event is dispatched immediately after all grimmix controllers
 * were located and constructed. This is the earliest you are allowed to
 * execute any of your grimmix' logic, like internal configuration or setup.<br>
 * The only exception is if you have an event handler that you want to
 * consistently receive {@link GrimmixValidationEvent}; in such cases you should
 * either subscribe it to {@link GrimoireAPI#EVENT_BUS} within constructor, or
 * use {@link CoreEventHandler} for the purpose of handling that event.<br><br>
 *
 * Furthermore, this event is a good time to ensure validity of your controller
 * in case in is designed to load only under specific conditions.
 * See {@link #invalidate()}.
 *
 * @author Aizistral
 * @see CoreEventHandler
 */

public interface IValidationEvent extends ILifecycleEvent {

    /**
     * Invalidates this grimmix controller. It will be dropped from the list
     * of loaded grimmixes, and Grimoire will make no further attempts to dispatch
     * lifecycle events to it. This is a good option to prevent your grimmix
     * from loading in case some specific conditions it requires are not satisfied.
     */
    public void invalidate();

    /**
     * @return True if this grimmix was invalidated already, false otherwise.
     */
    public boolean isValid();

}
