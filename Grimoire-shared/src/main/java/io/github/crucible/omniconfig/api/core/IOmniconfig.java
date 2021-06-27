package io.github.crucible.omniconfig.api.core;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

import io.github.crucible.omniconfig.api.lib.Version;
import io.github.crucible.omniconfig.api.properties.IAbstractProperty;

public interface IOmniconfig {

    public Collection<IAbstractProperty> getLoadedParameters();

    public Optional<IAbstractProperty> getParameter(String parameterID);

    public void forceReload();

    public boolean isReloadable();

    public File getFile();

    public String getFileName();

    public String getFileID();

    public Version getVersion();

    public SidedConfigType getSidedType();

}
