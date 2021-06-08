package io.github.crucible.omniconfig.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.base.Preconditions;

import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractBufferIO;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractPlayerMP;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractServer;
import io.github.crucible.omniconfig.lib.Finalized;
import io.github.crucible.omniconfig.wrappers.OmniconfigWrapper;
import io.github.crucible.omniconfig.wrappers.values.AbstractParameter;

public class SynchronizationManager {
    private static AbstractPacketDispatcher<?, ? extends AbstractPlayerMP<?>> dispatcher = null;

    public static void setPacketDispatcher(AbstractPacketDispatcher<?, ? extends AbstractPlayerMP<?>> instance) {
        Preconditions.checkArgument(dispatcher == null, "Packet dispatcher already set!");
        dispatcher = instance;
    }

    public static AbstractPacketDispatcher<?, ? extends AbstractPlayerMP<?>> getPacketDispatcher() {
        return dispatcher;
    }

    public static void syncToAll(OmniconfigWrapper wrapper) {
        if (wrapper.config.getSidedType().isSided())
            return;

        Optional.ofNullable(dispatcher.getServer()).ifPresent(server -> {
            server.forEachPlayer(player -> {
                if (player.areWeRemoteServer()) {
                    player.sendSyncPacket(wrapper);
                    OmniconfigCore.logger.info("Successfully resynchronized file " + wrapper.config.getConfigFile().getName() + " to " + player.getProfileName());
                } else {
                    OmniconfigCore.logger.info("File " + wrapper.config.getConfigFile().getName() + " was not resynchronized to " + player.getProfileName() + ", since this integrated server is hosted by them.");
                    OmniconfigWrapper.onRemoteServer = false;
                }
            });
        });
    }

    public static void syncToPlayer(OmniconfigWrapper wrapper, AbstractPlayerMP<?> player) {
        if (wrapper.config.getSidedType().isSided())
            return;

        if (player.areWeRemoteServer()) {
            OmniconfigCore.logger.info("Sending data for " + wrapper.config.getConfigFile().getName());
            player.sendSyncPacket(wrapper);
        } else {
            OmniconfigCore.logger.info("File " + wrapper.config.getConfigFile().getName() + " was not resynchronized to " + player.getProfileName() + ", since this integrated server is hosted by them.");
            OmniconfigWrapper.onRemoteServer = false;
        }
    }

    public static void syncAllToPlayer(AbstractPlayerMP<?> player) {
        if (player.areWeRemoteServer()) {
            OmniconfigCore.logger.info("Synchronizing omniconfig files to " + player.getProfileName() + "...");

            for (OmniconfigWrapper wrapper : OmniconfigWrapper.wrapperRegistry.values()) {
                if (!wrapper.config.getSidedType().isSided()) {
                    OmniconfigCore.logger.info("Sending data for " + wrapper.config.getConfigFile().getName());
                    player.sendSyncPacket(wrapper);
                }
            }
        } else {
            OmniconfigWrapper.onRemoteServer = false;
            OmniconfigCore.logger.info("Logging in to local integrated server; no synchronization is required.");
        }
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

    public static void dropRemoteConfigs() {
        if (OmniconfigWrapper.onRemoteServer) {
            /*
             * After we log out of remote server, dismiss config values it
             * sent us and load our own ones from local file.
             */

            OmniconfigWrapper.onRemoteServer = false;

            for (OmniconfigWrapper wrapper : OmniconfigWrapper.wrapperRegistry.values()) {
                OmniconfigCore.logger.info("Dismissing values of " + wrapper.config.getConfigFile().getName() + " in favor of local config...");

                wrapper.config.load();
                for (AbstractParameter<?> param : wrapper.retrieveInvocationList()) {
                    if (param.isSynchronized()) {
                        String oldValue = param.valueToString();
                        param.invoke(wrapper.config);

                        OmniconfigCore.logger.info("Value of '" + param.getId() + "' was restored to '" + param.valueToString() + "'; former server-forced value: " + oldValue);
                    }
                }
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
