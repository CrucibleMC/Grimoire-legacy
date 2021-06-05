package io.github.crucible.grimoire.mc1_7_10;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import io.github.crucible.grimoire.common.core.GrimoireCore;
import io.github.crucible.grimoire.mc1_7_10.integrations.eventhelper.EventHelperIntegration;

@Mod(modid = GrimoireMod.MODID)
public class GrimoireMod {
    public static final String MODID = "grimoire";

    public GrimoireMod() {
        // NO-OP
    }

    @Mod.EventHandler
    public final void serverStarted(FMLServerStartedEvent event) {
        //IntegrationManager.getEventHelperIntegration().initInjections();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Register our embedded integrations
        if (FMLCommonHandler.instance().getSide().isServer()) {
            GrimoireCore.INSTANCE.getGrimmixIntegrations().registerIntegration(new EventHelperIntegration());
        }
        //Init grimmix integrations
        GrimoireCore.INSTANCE.getGrimmixIntegrations().init();
    }

}