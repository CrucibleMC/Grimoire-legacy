package io.github.crucible.omniconfig.api.core;

import java.util.Collection;
import java.util.Optional;

import io.github.crucible.omniconfig.core.Omniconfig;

public interface IOmniconfigRegistry {

    public Collection<Omniconfig> getRegisteredConfigs();

    public Optional<Omniconfig> getConfig(String fileID);

}
