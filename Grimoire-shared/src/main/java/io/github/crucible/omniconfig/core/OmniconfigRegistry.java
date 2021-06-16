package io.github.crucible.omniconfig.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

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

    // TODO Replace Immutables with Collections.unmodifiable where plausible

    @Override
    public Collection<Omniconfig> getRegisteredConfigs() {
        return Collections.unmodifiableCollection(this.registryMap.values());
    }

    @Override
    public Optional<Omniconfig> getConfig(String fileID) {
        return Optional.ofNullable(this.registryMap.get(fileID));
    }


}
