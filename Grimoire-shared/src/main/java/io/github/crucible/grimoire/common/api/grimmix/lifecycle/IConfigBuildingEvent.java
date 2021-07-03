package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

import io.github.crucible.grimoire.common.api.mixin.IMixinConfigurationBuilder;

/**
 * This event allows you to create mixin configuration .json's at runtime.
 * It is important that they are built within the bounds of this event specifically,
 * since Grimoire will collect building products and generate appropriate .jar archive
 * containing all built configurations only once.
 *
 * @author Aizistral
 * @see IMixinConfigurationBuilder
 */

public interface IConfigBuildingEvent extends ILifecycleEvent {

    /**
     * Start new {@link IMixinConfigurationBuilder}.
     *
     * @param configurationPath Classpath your mixin configuration .json will have.
     * @return Fresh instance of {@link IMixinConfigurationBuilder}.
     * @see IMixinConfigurationBuilder
     */
    IMixinConfigurationBuilder createBuilder(String configurationPath);

}
