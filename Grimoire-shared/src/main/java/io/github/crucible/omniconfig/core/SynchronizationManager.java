package io.github.crucible.omniconfig.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.base.Preconditions;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.properties.IAbstractProperty;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractBufferIO;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractPlayerMP;
import io.github.crucible.omniconfig.core.properties.AbstractParameter;

public class SynchronizationManager {
    private static AbstractPacketDispatcher<?, ? extends AbstractPlayerMP<?>> dispatcher = null;

    public static void setPacketDispatcher(AbstractPacketDispatcher<?, ? extends AbstractPlayerMP<?>> instance) {
        Preconditions.checkArgument(dispatcher == null, "Packet dispatcher already set!");
        dispatcher = instance;
    }

    public static AbstractPacketDispatcher<?, ? extends AbstractPlayerMP<?>> getPacketDispatcher() {
        return dispatcher;
    }

    public static void syncToAll(Omniconfig wrapper) {
        if (wrapper.getSidedType().isSided())
            return;

        Optional.ofNullable(dispatcher.getServer()).ifPresent(server -> {
            server.forEachPlayer(player -> {
                if (player.areWeRemoteServer()) {
                    player.sendSyncPacket(wrapper);
                    OmniconfigCore.logger.info("Successfully resynchronized file " + wrapper.getFileID() + " to " + player.getProfileName());
                } else {
                    OmniconfigCore.logger.info("File " + wrapper.getFileID() + " was not resynchronized to " + player.getProfileName() + ", since this integrated server is hosted by them.");
                    OmniconfigCore.onRemoteServer = false;
                }
            });
        });
    }

    public static void syncToPlayer(Omniconfig wrapper, AbstractPlayerMP<?> player) {
        if (wrapper.getSidedType().isSided())
            return;

        if (player.areWeRemoteServer()) {
            OmniconfigCore.logger.info("Sending data for " + wrapper.getFileID());
            player.sendSyncPacket(wrapper);
        } else {
            OmniconfigCore.logger.info("File " + wrapper.getFileID() + " was not resynchronized to " + player.getProfileName() + ", since this integrated server is hosted by them.");
            OmniconfigCore.onRemoteServer = false;
        }
    }

    public static void syncAllToPlayer(AbstractPlayerMP<?> player) {
        if (player.areWeRemoteServer()) {
            OmniconfigCore.logger.info("Synchronizing omniconfig files to " + player.getProfileName() + "...");

            for (Omniconfig wrapper : OmniconfigRegistry.INSTANCE.getRegisteredConfigs()) {
                if (!wrapper.getSidedType().isSided()) {
                    OmniconfigCore.logger.info("Sending data for " + wrapper.getFileID());
                    player.sendSyncPacket(wrapper);
                }
            }
        } else {
            OmniconfigCore.onRemoteServer = false;
            OmniconfigCore.logger.info("Logging in to local integrated server; no synchronization is required.");
        }
    }

    public static void writeData(Omniconfig wrapper, AbstractBufferIO<?> io) {
        Map<String, String> synchronizedParameters = new HashMap<>();

        for (IAbstractProperty prop : wrapper.getLoadedParameters()) {
            AbstractParameter<?> param = (AbstractParameter<?>) prop;
            if (param.isSynchronized()) {
                synchronizedParameters.put(param.getID(), param.valueToString());
            }
        }

        io.writeString(wrapper.getFileID(), 512);
        io.writeString(String.valueOf(wrapper.getVersion()), 512);

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

    public static void updateData(Omniconfig wrapper, SyncData data) {
        OmniconfigCore.logger.info("Synchronizing values of " + data.fileID + " with ones dispatched by server...");

        for (Entry<String, String> entry : data.synchronizedParameters.entrySet()) {
            AbstractParameter<?> parameter = (AbstractParameter<?>) wrapper.getParameter(entry.getKey()).orElse(null);

            if (parameter != null) {
                String oldValue = parameter.valueToString();
                parameter.parseFromString(entry.getValue());

                OmniconfigCore.logger.info("Value of '" + parameter.getID() + "' was set to '" + parameter.valueToString() + "'; old value: " + oldValue);
            } else {
                OmniconfigCore.logger.error("Value '" + entry.getKey() + "' does not exist in " + data.fileID + "! Skipping.");
            }
        }
    }

    public static void dropRemoteConfigs() {
        if (OmniconfigCore.onRemoteServer) {
            /*
             * After we log out of remote server, dismiss config values it
             * sent us and load our own ones from local file.
             */

            OmniconfigCore.onRemoteServer = false;

            for (Omniconfig wrapper : OmniconfigRegistry.INSTANCE.getRegisteredConfigs()) {
                if (wrapper.getSidedType().isSided()) {
                    continue;
                }

                OmniconfigCore.logger.info("Dismissing values of " + wrapper.getFileID() + " in favor of local config...");

                wrapper.forceReload();
                for (IAbstractProperty prop : wrapper.getLoadedParameters()) {
                    AbstractParameter<?> param = (AbstractParameter<?>) prop;

                    if (param.isSynchronized()) {
                        String oldValue = param.valueToString();
                        param.reloadFrom(wrapper);

                        OmniconfigCore.logger.info("Value of '" + param.getID() + "' was restored to '" + param.valueToString() + "'; former server-forced value: " + oldValue);
                    }
                }
            }
        }
    }

    public static class SyncData {
        protected final String fileID, configVersion;
        protected final Map<String, String> synchronizedParameters;

        protected SyncData(String fileName, String configVersion, Map<String, String> params) {
            this.fileID = fileName;
            this.configVersion = configVersion;
            this.synchronizedParameters = params;
        }

        public String getFileID() {
            return this.fileID;
        }

        public String getConfigVersion() {
            return this.configVersion;
        }
    }

}
