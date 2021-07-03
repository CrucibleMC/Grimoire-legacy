package io.github.crucible.omniconfig.api.core;

import java.util.Collection;
import java.util.Optional;

import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;
import io.github.crucible.omniconfig.core.Omniconfig;

/**
 * Exposes some methods for interaction with full registry of
 * all {@link IOmniconfig} instances built up until this point.
 *
 * @author Aizistral
 * @see IOmniconfig
 * @see IOmniconfigBuilder
 */

public interface IOmniconfigRegistry {

    /**
     * @return All {@link IOmniconfig} instances that currently exist.
     * Returned collection is unmodifiable.
     * @see IOmniconfig
     */
    Collection<IOmniconfig> getRegisteredConfigs();

    /**
     * Try to locate {@link IOmniconfig} instance with supplied file ID.
     *
     * @param fileID ID in question.
     * @return {@link IOmniconfig} instance that has matching file ID,
     * or empty {@link Optional} if no such instance exists.
     * @see IOmniconfig#getFileID()
     */
    Optional<IOmniconfig> getConfig(String fileID);

}
