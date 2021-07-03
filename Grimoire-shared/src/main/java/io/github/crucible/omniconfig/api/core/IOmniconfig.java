package io.github.crucible.omniconfig.api.core;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;
import io.github.crucible.omniconfig.api.lib.Version;
import io.github.crucible.omniconfig.api.properties.IAbstractProperty;

/**
 * Reflects the state of omniconfig file created using {@link IOmniconfigBuilder},
 * once builder have finished execution and until the end of runtime.
 *
 * @author Aizistral
 * @see IOmniconfigBuilder
 */

public interface IOmniconfig {

    /**
     * @return Full list of properties associated with this config file.
     * Returned list is an unmodifiable collection.
     */
    Collection<IAbstractProperty> getLoadedProperties();

    /**
     * @param propertyID ID of property to try and locate.
     * @return Property associated with this config file, which has its
     * ID equal to passed argument. If no such property exists, an empty
     * {@link Optional} is returned instead.
     */
    Optional<IAbstractProperty> getProperty(String propertyID);

    /**
     * Force configuration to reload its property values from physical
     * config file, regardless of whether this configuration {@link #isReloadable()}
     * or not.
     */
    void forceReload();

    /**
     * @return True if this configuration is marked as reloadable and its
     * associated physical file is currently monitored for changes; false otherwise.
     */
    boolean isReloadable();

    /**
     * @return {@link File} representing physical file associated with this
     * configuration instance.
     */
    File getFile();

    /**
     * @return Equivalent to invokation <code>{@link #getFile()}.getName()</code>.
     */
    String getFileName();

    /**
     * @return {@link String} ID used to distinguish this config file among others.
     * Equals to the file name prefixed with entire directory path it has relative
     * to main config directory denoted by {@link OmniconfigAPI#getConfigFolder()}.
     */
    String getFileID();

    /**
     * @return {@link Version} of this config file.
     * @see Version
     */
    Version getVersion();

    /**
     * @return {@link SidedConfigType} this config file has.
     * @see SidedConfigType
     */
    SidedConfigType getSidedType();

}
