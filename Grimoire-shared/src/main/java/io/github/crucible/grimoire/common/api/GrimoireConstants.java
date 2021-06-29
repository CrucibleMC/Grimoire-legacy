package io.github.crucible.grimoire.common.api;

/**
 * Some things never change.
 *
 * @author Aizistral
 */

public class GrimoireConstants {

    private GrimoireConstants() {
        // Can't touch this
    }

    /**
     * Name that {@link GrimoireAPI#EVENT_BUS} will be registered with.
     */
    public static final String MAIN_BUS_NAME = "GrimoireMainBus";
}
