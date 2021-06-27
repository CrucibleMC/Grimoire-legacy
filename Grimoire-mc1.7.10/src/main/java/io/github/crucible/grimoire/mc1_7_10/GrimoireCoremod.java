package io.github.crucible.grimoire.mc1_7_10;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.lib.Environment;
import io.github.crucible.grimoire.common.events.SubscribeAnnotationWrapper;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadAnnotationWrapper;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadVersionHandler;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 1000)
public class GrimoireCoremod implements IFMLLoadingPlugin {

    @SuppressWarnings("deprecation")
    public GrimoireCoremod() {
        GrimoireCore.INSTANCE.setCoremodManager(CoreModManager.class);

        MixinBootstrap.init();
        Mixins.addConfiguration("grimoire/mixins.grimoire.json");
    }

    @Override
    public void injectData(Map<String, Object> data) {
        GrimoireCore.INSTANCE.configure((File) data.get("mcLocation"), "mods", "1.7.10",
                FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT ? Environment.CLIENT : Environment.DEDICATED_SERVER);
        SubscribeAnnotationWrapper.setWrapperFactory(this::createWrapper);
        ChadVersionHandler.init();

        GrimoireCore.INSTANCE.init();
    }

    private SubscribeAnnotationWrapper createWrapper(Method method) {
        return new ChadAnnotationWrapper(method.getAnnotation(cpw.mods.fml.common.eventhandler.SubscribeEvent.class));
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
