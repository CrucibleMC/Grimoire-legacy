package io.github.crucible.grimoire.mc1_7_10.network;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadPacketDispatcher;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadPacketDispatcher.ChadBufferIO;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.lib.Either;
import io.github.crucible.omniconfig.core.Omniconfig;
import io.github.crucible.omniconfig.core.OmniconfigRegistry;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.core.SynchronizationManager.SyncData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class PacketSyncOmniconfig implements IMessage {
    private final ChadPacketDispatcher dispatcher = ChadPacketDispatcher.INSTANCE;
    private Either<Omniconfig, SyncData> either;

    public PacketSyncOmniconfig(@NotNull Omniconfig wrapper) {
        this.either = Either.fromA(Objects.requireNonNull(wrapper));
    }

    public PacketSyncOmniconfig() {
        // NO-OP
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.either = Either.fromB(SynchronizationManager.readData(this.dispatcher.getBufferIO(buf)));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        SynchronizationManager.writeData(this.either.getA(), this.dispatcher.getBufferIO(buf));
    }

    public static class Handler implements IMessageHandler<PacketSyncOmniconfig, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSyncOmniconfig message, MessageContext ctx) {
            OmniconfigCore.onRemoteServer = true;

            message.either.ifB(data ->
            OmniconfigRegistry.INSTANCE.getConfig(data.getFileID()).ifPresent(wrapper ->
            SynchronizationManager.updateData(wrapper, data)));

            return null;
        }
    }

}

