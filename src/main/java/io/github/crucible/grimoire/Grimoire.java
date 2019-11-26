package io.github.crucible.grimoire;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.crucible.blackmagic.BlackMagic;
import io.github.crucible.blackmagic.core.FileLoader;
import io.github.crucible.grimoire.core.MixinModLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class Grimoire implements IFMLLoadingPlugin, IMixinConfigPlugin {

    private static Logger logger = Logger.getLogger("Grimoire");
    private static Grimoire instance;

    public Grimoire(){
        BlackMagic.inject(this.getClass(), new FileLoader(this.getClass(), "libs"));
        System.setProperty("mixin.debug.export", "true");
        MixinBootstrap.init();
        instance = this;

        new MixinModLoader().init();

    }

    public static Grimoire getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return logger;
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
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

}
