package io.github.crucible.grimoire.mc1_7_10;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.integrations.ModIntegrationRegistry;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadEventHandler;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadPacketDispatcher;
import io.github.crucible.grimoire.mc1_7_10.integration.eventhelper.EHIntegrationContainer;
import io.github.crucible.grimoire.mc1_7_10.network.PacketSyncOmniconfig;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = GrimoireMod.MODID, name = GrimoireMod.NAME, version = GrimoireMod.VERSION)
public class GrimoireMod {
    public static final String MODID = "grimoire";
    public static final String NAME = "Grimoire";
    public static final String VERSION = "@VERSION@";

    public static SimpleNetworkWrapper packetPipeline;

    public GrimoireMod() {
        // NO-OP

        GrimoireCore.logger.info("Mod instance constructed!");
    }

    @Mod.EventHandler
    public final void serverStarted(FMLServerStartedEvent event) {
        // IntegrationManager.getEventHelperIntegration().initInjections();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GrimoireCore.logger.info("Pre initialization happened!");

        ChadPacketDispatcher.INSTANCE.getClass();

        ChadEventHandler handler = new ChadEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);

        packetPipeline = new SimpleNetworkWrapper("GrimoireChannel");
        packetPipeline.registerMessage(PacketSyncOmniconfig.Handler.class, PacketSyncOmniconfig.class, 0, Side.CLIENT);

        // Register our embedded integrations
        ModIntegrationRegistry.registerIntegration(EHIntegrationContainer.class);
        ModIntegrationRegistry.init();
    }

}