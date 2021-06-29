package io.github.crucible.grimoire.common.api.grimmix;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;

/**
 * Abstract class that all grimmix controllers decorated with {@link Grimmix}
 * annotation must inherit. Contains a variety of methods for receiving lifecycle
 * events; any of these methods can be overriden to do something with respective
 * events they receive.
 *
 * @author Aizistral
 */

public abstract class GrimmixController {

    /**
     * Every controller should have at least one public constructor
     * that requires no arguments, for Grimoire itself will take
     * care of instantiating it. Keep that in mind.
     */
    public GrimmixController() {
        // NO-OP
    }

    /**
     * Receives and handles {@link IValidationEvent}.
     * @param event Event instance.
     * @see IValidationEvent
     */
    public void validateController(IValidationEvent event) {
        // NO-OP
    }

    /**
     * Receives and handles {@link IConfigBuildingEvent}
     * @param event Event instance.
     * @see IConfigBuildingEvent
     */
    public void buildMixinConfigs(IConfigBuildingEvent event) {
        // NO-OP
    }

    /**
     * Receives and handles {@link ICoreLoadEvent}
     * @param event Event instance.
     * @see ICoreLoadEvent
     */
    public void coreLoad(ICoreLoadEvent event) {
        // NO-OP
    }

    /**
     * Receives and handles {@link IModLoadEvent}
     * @param event Event instance.
     * @see IModLoadEvent
     */
    public void modLoad(IModLoadEvent event) {
        // NO-OP
    }

    /**
     * Receives and handles {@link IFinishLoadEvent}
     * @param event Event instance.
     * @see IFinishLoadEvent
     */
    public void finish(IFinishLoadEvent event) {
        // NO-OP
    }

}
