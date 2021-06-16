package io.github.crucible.grimoire.mc1_12.handlers;

import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.grimoire.mc1_12.handlers.IncelPacketDispatcher.IncelPlayerMP;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IncelEventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEntityTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = Minecraft.getMinecraft().player;

            /*
             * This event seems to be the only way to detect player logging out of server
             * on client side, since PlayerEvent.PlayerLoggedOutEvent seems to only be fired
             * on the server side.
             */

            if (player == null) {
                SynchronizationManager.dropRemoteConfigs();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP))
            return;

        GrimoireInternals.ifInstance(event.player, EntityPlayerMP.class, player ->
        SynchronizationManager.syncAllToPlayer(new IncelPlayerMP(player)));
    }

}
