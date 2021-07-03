package io.github.crucible.omniconfig.api.annotation;

import java.util.Collection;
import java.util.Optional;

import io.github.crucible.omniconfig.api.core.IOmniconfig;

/**
 * Exposes some options for interating with annotation config registry.
 *
 * @author Aizistral
 */

public interface IAnnotationConfigRegistry {

    /**
     * @return Collections of valid classes that were registered as annotation
     * configurations until this point. Unmodifiable.
     */
    Collection<Class<?>> getRegisteredAnnotationConfigs();

    /**
     * @param annotationConfigClass Class which supposedly was already registered as
     * annotation config class.
     * @return If it indeed was registered, returns instance of {@link IOmniconfig}
     * associated with that class; or empty {@link Optional} if it wasn't.
     */
    Optional<IOmniconfig> getAssociatedOmniconfig(Class<?> annotationConfigClass);

}
