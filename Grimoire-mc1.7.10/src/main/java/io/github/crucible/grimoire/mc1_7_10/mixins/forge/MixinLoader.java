package io.github.crucible.grimoire.mc1_7_10.mixins.forge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.transformer.Proxy;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModClassLoader;
import cpw.mods.fml.common.ModContainer;
import io.github.crucible.grimoire.common.GrimoireCore;
import net.minecraft.launchwrapper.Launch;

/**
 * This is not the ideal way to do it, pretend this does not even exist.
 */
@Mixin(value = Loader.class, remap = false)
public class MixinLoader {

    @Shadow
    private List<ModContainer> mods;
    @Shadow
    private ModClassLoader modClassLoader;

    /**
     * @reason Load all mods now and load mod support mixin configs. This can't be done later
     * since constructing mods loads classes from them.
     */
    @Inject(method = "loadMods", at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/LoadController;transition(Lcpw/mods/fml/common/LoaderState;Z)V", ordinal = 1), remap = false)
    private void beforeConstructingMods(CallbackInfo callbackInfo) {

        if (GrimoireCore.INSTANCE.prepareModConfigs().size() <= 0) {
            GrimoireCore.logger.info("Since 0 mod-targeting configurations is present, skipping forced Mixin environment reload.");
            GrimoireCore.INSTANCE.loadModConfigs();
            GrimoireCore.INSTANCE.finish();
            return;
        } else {
            // Add all mods to class loader
            GrimoireCore.logger.info("Adding all mods to classpath...");

            for (ModContainer mod : this.mods) {
                try {
                    this.modClassLoader.addFile(mod.getSource());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }

            GrimoireCore.logger.info("Proccessing mod-targeting configurations...");
            GrimoireCore.INSTANCE.loadModConfigs(); // Add all mod configurations

            GrimoireCore.logger.info("Attempting to forcefully reload configurations for current Mixin environment...");
            // Force configurations to load once more
            Proxy mixinProxy = (Proxy) Launch.classLoader.getTransformers().stream().filter(transformer -> transformer instanceof Proxy).findFirst().get();
            try {
                // This will very likely break on the next major mixin release.
                Class<?> mixinTransformerClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");

                Field transformerField = Proxy.class.getDeclaredField("transformer");
                transformerField.setAccessible(true);
                Object transformer = transformerField.get(mixinProxy);

                // Get MixinProcessor from MixinTransformer
                Field processorField = mixinTransformerClass.getDeclaredField("processor");
                processorField.setAccessible(true);
                Object processor = processorField.get(transformer);
                Class<?> processorClass = processor.getClass();

                Method selectConfigsMethod = processorClass.getDeclaredMethod("selectConfigs", MixinEnvironment.class);
                selectConfigsMethod.setAccessible(true);
                selectConfigsMethod.invoke(processor, MixinEnvironment.getCurrentEnvironment());

                Method prepareConfigsMethod = processorClass.getDeclaredMethod("prepareConfigs", MixinEnvironment.class);
                prepareConfigsMethod.setAccessible(true);
                prepareConfigsMethod.invoke(processor, MixinEnvironment.getCurrentEnvironment());
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }

            GrimoireCore.logger.info("Configurations reloaded successfully.");
            GrimoireCore.INSTANCE.finish(); // Dispatch final lifecycle events
        }
    }
}
