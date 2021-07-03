package io.github.crucible.grimoire.mc1_7_10.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadPacketDispatcher;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.lib.Either;
import io.github.crucible.omniconfig.core.Omniconfig;
import io.github.crucible.omniconfig.core.OmniconfigRegistry;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.core.SynchronizationManager.SyncData;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PacketSyncOmniconfig implements IMessage {
    private final ChadPacketDispatcher dispatcher = ChadPacketDispatcher.INSTANCE;
    private Either<Omniconfig, SyncData> either;

    public PacketSyncOmniconfig(@NotNull Omniconfig wrapper) {
        this.either = Either.fromFirst(Objects.requireNonNull(wrapper));
    }

    public PacketSyncOmniconfig() {
        // NO-OP
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.either = Either.fromSecond(SynchronizationManager.readData(this.dispatcher.getBufferIO(buf)));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        SynchronizationManager.writeData(this.either.getFirst(), this.dispatcher.getBufferIO(buf));
    }

    public static class Handler implements IMessageHandler<PacketSyncOmniconfig, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSyncOmniconfig message, MessageContext ctx) {
            OmniconfigCore.onRemoteServer = true;

            message.either.ifSecond(data ->
            OmniconfigRegistry.INSTANCE.getConfig(data.getFileID()).ifPresent(wrapper ->
            SynchronizationManager.updateData((Omniconfig) wrapper, data)));

            return null;
        }
    }

}

