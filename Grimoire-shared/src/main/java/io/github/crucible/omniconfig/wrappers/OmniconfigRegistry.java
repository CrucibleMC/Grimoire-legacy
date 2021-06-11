package io.github.crucible.omniconfig.wrappers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class OmniconfigRegistry {
    public static final OmniconfigRegistry INSTANCE = new OmniconfigRegistry();

    protected final Map<String, Omniconfig> registryMap = new HashMap<>();

    private OmniconfigRegistry() {
        // NO-OP
    }

    public void registerConfig(Omniconfig config) {
        if (!this.registryMap.containsKey(config.getFileID())) {
            this.registryMap.put(config.getFileID(), config);
        } else
            throw new RuntimeException("Attempted to register two config files with the same location: " + config.getFile());
    }

    // TODO Replace Immutables with Collections.unmodifiable where plausible

    public Collection<Omniconfig> getRegisteredConfigs() {
        return Collections.unmodifiableCollection(this.registryMap.values());
    }

    public Optional<Omniconfig> getConfig(String fileID) {
        return Optional.ofNullable(this.registryMap.get(fileID));
    }


}
