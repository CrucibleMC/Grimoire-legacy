package io.github.crucible.grimoire.mc1_7_10.handlers;

import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.grimoire.mc1_7_10.GrimoireMod;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.wrappers.OmniconfigWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import io.github.crucible.grimoire.mc1_7_10.network.PacketSyncOmniconfig;

public class ChadPacketDispatcher extends AbstractPacketDispatcher<ByteBuf, EntityPlayerMP> {
    public static final ChadPacketDispatcher INSTANCE = new ChadPacketDispatcher();

    private ChadPacketDispatcher() {
        SynchronizationManager.setPacketDispatcher(this);
    }

    @Override
    public ChadBufferIO getBufferIO(ByteBuf buffer) {
        return new ChadBufferIO(buffer);
    }

    @Override
    public void syncToAll(OmniconfigWrapper wrapper) {
        if (wrapper.config.getSidedType().isSided())
            return;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (server != null && server.getConfigurationManager() != null) {
            for (Object probablyPlayer : server.getConfigurationManager().playerEntityList) {
                GrimoireInternals.ifInstance(probablyPlayer, EntityPlayerMP.class, player -> {
                    if (this.areWeRemoteServer(player)) {
                        GrimoireMod.packetPipeline.sendTo(new PacketSyncOmniconfig(wrapper), player);
                        OmniconfigCore.logger.info("Successfully resynchronized file " + wrapper.config.getConfigFile().getName() + " to " + player.getGameProfile().getName());
                    } else {
                        OmniconfigCore.logger.info("File " + wrapper.config.getConfigFile().getName() + " was not resynchronized to " + player.getGameProfile().getName() + ", since this integrated server is hosted by them.");
                        OmniconfigWrapper.onRemoteServer = false;
                    }
                });
            }
        }
    }

    @Override
    public void syncToPlayer(OmniconfigWrapper wrapper, EntityPlayerMP player) {
        if (wrapper.config.getSidedType().isSided())
            return;

        if (this.areWeRemoteServer(player)) {
            OmniconfigCore.logger.info("Sending data for " + wrapper.config.getConfigFile().getName());
            GrimoireMod.packetPipeline.sendTo(new PacketSyncOmniconfig(wrapper), player);
        } else {
            OmniconfigCore.logger.info("File " + wrapper.config.getConfigFile().getName() + " was not resynchronized to " + player.getGameProfile().getName() + ", since this integrated server is hosted by them.");
            OmniconfigWrapper.onRemoteServer = false;
        }

    }

    @Override
    public void syncAllToPlayer(EntityPlayerMP player) {
        if (this.areWeRemoteServer(player)) {
            OmniconfigCore.logger.info("Synchronizing omniconfig files to " + player.getGameProfile().getName() + "...");

            for (OmniconfigWrapper wrapper : OmniconfigWrapper.wrapperRegistry.values()) {
                if (!wrapper.config.getSidedType().isSided()) {
                    OmniconfigCore.logger.info("Sending data for " + wrapper.config.getConfigFile().getName());
                    GrimoireMod.packetPipeline.sendTo(new PacketSyncOmniconfig(wrapper), player);
                }
            }

        } else {
            OmniconfigWrapper.onRemoteServer = false;
            OmniconfigCore.logger.info("Logging in to local integrated server; no synchronization is required.");
        }
    }

    private boolean areWeRemoteServer(EntityPlayerMP player) {
        if (GrimoireInternals.getEnvironment() == io.github.crucible.grimoire.common.api.lib.Side.DEDICATED_SERVER)
            return true;
        else
            return player.mcServer != null && !player.getGameProfile().getName().equals(player.mcServer.getServerOwner());
    }

    public static class ChadBufferIO extends AbstractBufferIO<ByteBuf> {

        protected ChadBufferIO(ByteBuf buffer) {
            super(buffer);
        }

        @Override
        public void writeString(String string, int charLimit) {
            if (string != null && string.length() > 0 && string.length() <= charLimit) {
                byte[] bytes = string.getBytes(Charsets.UTF_8);
                this.buffer.writeShort(bytes.length);
                this.buffer.writeBytes(bytes);
            } else {
                if (string != null && string.length() >= charLimit) {
                    Throwables.propagate(new IOException("Encoded string size " + string.length() + " is larger than allowed maximum " + charLimit));
                } else {
                    this.buffer.writeShort(-1);
                }
            }
        }

        @Override
        public void writeLong(long value) {
            this.buffer.writeLong(value);
        }

        @Override
        public void writeInt(int value) {
            this.buffer.writeInt(value);
        }

        @Override
        public void writeDouble(double value) {
            this.buffer.writeDouble(value);
        }

        @Override
        public void writeFloat(float value) {
            this.buffer.writeFloat(value);
        }

        @Override
        public String readString(int charLimit) {
            short size = this.buffer.readShort();

            if (size < 0)
                return "null";

            String str = new String(this.buffer.readBytes(size).array(), Charsets.UTF_8);


            if (str.length() <= charLimit)
                return str;
            else {
                Throwables.propagate(new IOException("Received string size " + str.length() + " is larger than allowed maximum " + charLimit));
            }

            return "null";
        }

        @Override
        public long readLong() {
            return this.buffer.readLong();
        }

        @Override
        public int readInt() {
            return this.buffer.readInt();
        }

        @Override
        public double readDouble() {
            return this.buffer.readDouble();
        }

        @Override
        public float readFloat() {
            return this.buffer.readFloat();
        }

    }

}
