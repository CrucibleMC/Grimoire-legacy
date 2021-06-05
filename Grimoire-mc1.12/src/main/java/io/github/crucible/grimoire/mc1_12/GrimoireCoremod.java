package io.github.crucible.grimoire.mc1_12;

import io.github.crucible.grimoire.common.api.lib.Side;
import io.github.crucible.grimoire.common.core.GrimoireCore;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 1000)
public class GrimoireCoremod implements IFMLLoadingPlugin {
    public GrimoireCoremod() {
        GrimoireCore.INSTANCE.getClass(); // Make it construct
        MixinBootstrap.init();
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
    public void injectData(Map<String, Object> data) {
        GrimoireCore.INSTANCE.configure((File) data.get("mcLocation"),
                (Boolean) data.get("runtimeDeobfuscationEnabled"), "mods", "1.12",
                FMLLaunchHandler.side() == net.minecraftforge.fml.relauncher.Side.CLIENT ? Side.CLIENT : Side.DEDICATED_SERVER);

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
