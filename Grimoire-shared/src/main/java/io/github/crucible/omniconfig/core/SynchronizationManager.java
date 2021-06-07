package io.github.crucible.omniconfig.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.base.Preconditions;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractBufferIO;
import io.github.crucible.omniconfig.lib.Finalized;
import io.github.crucible.omniconfig.wrappers.OmniconfigWrapper;
import io.github.crucible.omniconfig.wrappers.values.AbstractParameter;

public class SynchronizationManager {
    private static AbstractPacketDispatcher<?, ?> dispatcher = null;

    public static void setPacketDispatcher(AbstractPacketDispatcher<?, ?> instance) {
        Preconditions.checkArgument(dispatcher == null, "Packet dispatcher already set!");
        dispatcher = instance;
    }

    public static AbstractPacketDispatcher<?, ?> getPacketDispatcher() {
        return dispatcher;
    }

    public static void syncToAll(OmniconfigWrapper wrapper) {
        dispatcher.syncToAll(wrapper);
    }

    public static Optional<OmniconfigWrapper> getWrapper(String fileName) {
        return Optional.ofNullable(OmniconfigWrapper.wrapperRegistry.get(fileName));
    }

    public static void writeData(OmniconfigWrapper wrapper, AbstractBufferIO<?> io) {
        Map<String, String> synchronizedParameters = new HashMap<>();

        for (AbstractParameter<?> param : wrapper.retrieveInvocationList()) {
            if (param.isSynchronized()) {
                synchronizedParameters.put(param.getId(), param.valueToString());
            }
        }

        io.writeString(wrapper.config.getConfigFile().getName(), 512);
        io.writeString(String.valueOf(wrapper.config.getLoadedConfigVersion()), 512);

        io.writeLong(synchronizedParameters.size());

        for (Entry<String, String> entry : synchronizedParameters.entrySet()) {
            io.writeString(entry.getKey(), 512);
            io.writeString(entry.getValue(), 32768);
        }
    }

    public static SyncData readData(AbstractBufferIO<?> io) {
        String fileName = io.readString(512);

        String configVersion = io.readString(512);
        long entryAmount = io.readLong();

        Map<String, String> params = new HashMap<>();

        for (int counter = 0; counter < entryAmount; counter++) {
            String identifier = io.readString(512);
            String value = io.readString(32768);

            params.put(identifier, value);
        }

        return new SyncData(fileName, configVersion, params);
    }

    public static void updateData(OmniconfigWrapper wrapper, SyncData data) {
        OmniconfigCore.logger.info("Synchronizing values of " + data.fileName + " with ones dispatched by server...");

        for (Entry<String, String> entry : data.synchronizedParameters.entrySet()) {
            AbstractParameter<?> parameter = wrapper.invokationMap.get(entry.getKey());

            if (parameter != null) {
                String oldValue = parameter.valueToString();
                parameter.parseFromString(entry.getValue());

                OmniconfigCore.logger.info("Value of '" + parameter.getId() + "' was set to '" + parameter.valueToString() + "'; old value: " + oldValue);
            } else {
                OmniconfigCore.logger.error("Value '" + entry.getKey() + "' does not exist in " + data.fileName + "! Skipping.");
            }
        }
    }

    public static class SyncData {
        protected final String fileName, configVersion;
        protected final Map<String, String> synchronizedParameters;

        protected SyncData(String fileName, String configVersion, Map<String, String> params) {
            this.fileName = fileName;
            this.configVersion = configVersion;
            this.synchronizedParameters = params;
        }

        public String getFileName() {
            return this.fileName;
        }

        public String getConfigVersion() {
            return this.configVersion;
        }
    }

}
