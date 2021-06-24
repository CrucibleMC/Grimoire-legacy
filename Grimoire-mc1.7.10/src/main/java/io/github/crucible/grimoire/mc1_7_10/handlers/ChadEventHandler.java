package io.github.crucible.grimoire.mc1_7_10.handlers;

import com.gamerforea.eventhelper.util.ConvertUtils;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.grimoire.common.core.GrimoireCore;
import io.github.crucible.grimoire.common.integrations.ModIntegrationRegistry;
import io.github.crucible.grimoire.mc1_7_10.api.integration.eventhelper.IEventHelperIntegration;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadPacketDispatcher.ChadPlayerMP;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Omniconfig;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.core.properties.AbstractParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.ChunkEvent;

public class ChadEventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEntityTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;

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
        SynchronizationManager.syncAllToPlayer(new ChadPlayerMP(player)));
    }

    /*
    @SubscribeEvent
    public void onTick(ChunkEvent.Load event) {
        GrimoireCore.logger.info("Chunk loaded!");

        Essentials plugin = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        IJails jails = plugin.getJails();
        GrimoireCore.logger.info("Blast, it works! " + jails.getClass());
    }
     */

}
