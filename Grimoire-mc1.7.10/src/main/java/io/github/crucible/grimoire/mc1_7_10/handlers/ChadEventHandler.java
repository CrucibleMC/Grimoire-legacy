package io.github.crucible.grimoire.mc1_7_10.handlers;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.wrappers.OmniconfigWrapper;
import io.github.crucible.omniconfig.wrappers.values.AbstractParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP))
            return;

        GrimoireInternals.ifInstance(event.player, EntityPlayerMP.class, player ->
        ChadPacketDispatcher.INSTANCE.syncAllToPlayer(player));
    }

}
