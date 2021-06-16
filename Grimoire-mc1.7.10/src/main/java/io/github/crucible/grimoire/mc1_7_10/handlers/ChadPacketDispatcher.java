package io.github.crucible.grimoire.mc1_7_10.handlers;

import java.io.IOException;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.grimoire.common.api.lib.Side;
import io.github.crucible.grimoire.mc1_7_10.GrimoireMod;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher;
import io.github.crucible.omniconfig.core.Omniconfig;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractPlayerMP;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import io.github.crucible.grimoire.mc1_7_10.network.PacketSyncOmniconfig;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadPacketDispatcher.ChadPlayerMP;

public class ChadPacketDispatcher extends AbstractPacketDispatcher<ByteBuf, ChadPlayerMP> {
    public static final ChadPacketDispatcher INSTANCE = new ChadPacketDispatcher();

    private ChadPacketDispatcher() {
        SynchronizationManager.setPacketDispatcher(this);
    }

    @Override
    public ChadBufferIO getBufferIO(ByteBuf buffer) {
        return new ChadBufferIO(buffer);
    }

    @Override
    public ChadServer getServer() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        return server != null && server.getConfigurationManager() != null ? new ChadServer(server) : null;
    }

    public static class ChadServer extends AbstractServer<MinecraftServer, ChadPlayerMP> {
        public ChadServer(MinecraftServer server) {
            super(server);
        }

        @Override
        public void forEachPlayer(Consumer<ChadPlayerMP> consumer) {
            this.server.getConfigurationManager().playerEntityList.forEach(probablyPlayer -> {
                GrimoireInternals.ifInstance(probablyPlayer, EntityPlayerMP.class, player -> {
                    consumer.accept(new ChadPlayerMP(player));
                });
            });
        }
    }

    public static class ChadPlayerMP extends AbstractPlayerMP<EntityPlayerMP> {
        public ChadPlayerMP(EntityPlayerMP player) {
            super(player);
        }

        @Override
        public void sendSyncPacket(Omniconfig wrapper) {
            GrimoireMod.packetPipeline.sendTo(new PacketSyncOmniconfig(wrapper), this.player);
        }

        @Override
        public boolean areWeRemoteServer() {
            if (GrimoireInternals.getEnvironment() == Side.DEDICATED_SERVER)
                return true;
            else
                return this.player.mcServer != null && !this.player.getGameProfile().getName().equals(this.player.mcServer.getServerOwner());
        }

        @Override
        public String getProfileName() {
            return this.player.getGameProfile().getName();
        }
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
