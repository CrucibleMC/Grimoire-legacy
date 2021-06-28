package io.github.crucible.omniconfig.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.github.crucible.omniconfig.api.core.IOmniconfig;
import io.github.crucible.omniconfig.api.core.IOmniconfigRegistry;

public class OmniconfigRegistry implements IOmniconfigRegistry {
    public static final OmniconfigRegistry INSTANCE = new OmniconfigRegistry();

    protected final Map<String, Omniconfig> registryMap = new HashMap<>();

    private OmniconfigRegistry() {
        // NO-OP
    }

    protected void registerConfig(Omniconfig config) {
        if (!this.registryMap.containsKey(config.getFileID())) {
            this.registryMap.put(config.getFileID(), config);
        } else
            throw new RuntimeException("Attempted to register two config files with the same location: " + config.getFile());
    }

    @Override
    public Collection<IOmniconfig> getRegisteredConfigs() {
        return Collections.unmodifiableCollection(this.registryMap.values());
    }

    @Override
    public Optional<IOmniconfig> getConfig(String fileID) {
        return Optional.ofNullable(this.registryMap.get(fileID));
    }


}
