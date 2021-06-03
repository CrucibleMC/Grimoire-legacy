package io.github.crucible.grimoire;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.crucible.grimoire.core.GrimmixLoader;
import net.minecraft.launchwrapper.LaunchClassLoader;
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
        Grimoire.construct();
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins/mixins.grimoire.json");

        LogManager.getLogger("GrimoireCore").info("Coremod construtced!");
    }

    @Override
    public void injectData(Map<String, Object> data) {
        Grimoire.configure((File) data.get("mcLocation"),
                (Boolean) data.get("runtimeDeobfuscationEnabled"),
                "mods",
                "1.7.10");
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
