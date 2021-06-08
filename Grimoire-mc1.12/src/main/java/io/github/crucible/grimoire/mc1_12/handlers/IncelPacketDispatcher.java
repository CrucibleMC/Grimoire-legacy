package io.github.crucible.grimoire.mc1_12.handlers;

import java.io.IOException;
import java.util.function.Consumer;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.grimoire.common.api.lib.Side;
import io.github.crucible.grimoire.mc1_12.GrimoireMod;
import io.github.crucible.grimoire.mc1_12.network.PacketSyncOmniconfig;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractBufferIO;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractPlayerMP;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher.AbstractServer;
import io.github.crucible.omniconfig.wrappers.OmniconfigWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import io.github.crucible.grimoire.mc1_12.handlers.IncelPacketDispatcher.IncelPlayerMP;

public class IncelPacketDispatcher extends AbstractPacketDispatcher<ByteBuf, IncelPlayerMP> {
    public static final IncelPacketDispatcher INSTANCE = new IncelPacketDispatcher();

    private IncelPacketDispatcher() {
        SynchronizationManager.setPacketDispatcher(this);
    }

    @Override
    public IncelBufferIO getBufferIO(ByteBuf buffer) {
        return new IncelBufferIO(buffer);
    }

    @Override
    public IncelServer getServer() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        return server != null && server.getPlayerList() != null ? new IncelServer(server) : null;
    }

    public static class IncelServer extends AbstractServer<MinecraftServer, IncelPlayerMP> {
        public IncelServer(MinecraftServer server) {
            super(server);
        }

        @Override
        public void forEachPlayer(Consumer<IncelPlayerMP> consumer) {
            this.server.getPlayerList().getPlayers().forEach(player -> {
                consumer.accept(new IncelPlayerMP(player));
            });
        }
    }

    public static class IncelPlayerMP extends AbstractPlayerMP<EntityPlayerMP> {
        public IncelPlayerMP(EntityPlayerMP player) {
            super(player);
        }

        @Override
        public void sendSyncPacket(OmniconfigWrapper wrapper) {
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

    public static class IncelBufferIO extends AbstractBufferIO<ByteBuf> {
        protected IncelBufferIO(ByteBuf buffer) {
            super(buffer);
        }

        @Override
        public void writeString(String string, int charLimit) {
            if (string != null && string.length() > 0 && string.length() <= charLimit) {
                byte[] bytes = string.getBytes(Charsets.UTF_8);
                this.buffer.writeShort(bytes.length);
                this.buffer.writeBytes(bytes);
            } else {
                if (string != null && string.length() >= charLimit)
                    throw new RuntimeException(new IOException("Encoded string size " + string.length() + " is larger than allowed maximum " + charLimit));
                else {
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
            else
                throw new RuntimeException(new IOException("Received string size " + str.length() + " is larger than allowed maximum " + charLimit));
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
