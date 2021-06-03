package io.github.crucible.grimoire;

import net.minecraftforge.common.ForgeVersion;
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
        Grimoire.construct();
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

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        Grimoire.configure((File) data.get("mcLocation"),
                (Boolean) data.get("runtimeDeobfuscationEnabled"),
                "mods",
                "1.12");

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
