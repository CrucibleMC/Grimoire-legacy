package io.github.crucible.omniconfig.api.builders;

/**
 * Represents phases in which {@link IOmniconfigBuilder} does its work.
 *
 * @author Aizistral
 * @see IOmniconfigBuilder
 */

public enum BuildingPhase {
    /**
     * Initialization phase of omniconfig building.
     * @see IOmniconfigBuilder
     */
    INITIALIZATION,

    /**
     * Phase for loading actual properties in omniconfig building.
     * @see IOmniconfigBuilder
     */
    PROPERTY_LOADING,

    /**
     * Final phase of omniconfig building, after all actual properties
     * are already loaded.
     * @see IOmniconfigBuilder
     */
    FINALIZATION;
}