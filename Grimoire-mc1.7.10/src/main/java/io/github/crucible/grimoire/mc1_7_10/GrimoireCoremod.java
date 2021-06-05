package io.github.crucible.grimoire.mc1_7_10;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.crucible.grimoire.common.api.lib.Side;
import io.github.crucible.grimoire.common.core.GrimoireCore;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 1000)
public class GrimoireCoremod implements IFMLLoadingPlugin {
    public GrimoireCoremod() {
        GrimoireCore.INSTANCE.getClass(); // Make it construct

        MixinBootstrap.init();
        Mixins.addConfiguration("grimoire/mixins.forge.json");

        LogManager.getLogger("GrimoireCore").info("Coremod construtced!");
    }

    @Override
    public void injectData(Map<String, Object> data) {
        GrimoireCore.INSTANCE.configure((File) data.get("mcLocation"),
                (Boolean) data.get("runtimeDeobfuscationEnabled"), "mods", "1.7.10",
                FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT ? Side.CLIENT : Side.DEDICATED_SERVER);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
