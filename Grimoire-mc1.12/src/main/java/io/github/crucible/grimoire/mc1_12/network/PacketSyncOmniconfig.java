package io.github.crucible.grimoire.mc1_12.network;

import java.util.Objects;

import javax.annotation.Nonnull;

import io.github.crucible.grimoire.mc1_12.handlers.IncelPacketDispatcher;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.core.SynchronizationManager.SyncData;
import io.github.crucible.omniconfig.lib.Either;
import io.github.crucible.omniconfig.wrappers.OmniconfigWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncOmniconfig implements IMessage {
    private final IncelPacketDispatcher dispatcher = IncelPacketDispatcher.INSTANCE;
    private Either<OmniconfigWrapper, SyncData> either;

    public PacketSyncOmniconfig(@Nonnull OmniconfigWrapper wrapper) {
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
            OmniconfigWrapper.onRemoteServer = true;
            OmniconfigCore.logger.info("Receiving data from remote server. SyncData preset: " + message.either.isB()
            + ", file: " + message.either.getB().getFileName() + ", wrapper present: " + SynchronizationManager.getWrapper(message.either.getB().getFileName()).isPresent());

            message.either.ifB(data ->
            SynchronizationManager.getWrapper(data.getFileName()).ifPresent(wrapper ->
            SynchronizationManager.updateData(wrapper, data)));

            return null;
        }
    }

}


