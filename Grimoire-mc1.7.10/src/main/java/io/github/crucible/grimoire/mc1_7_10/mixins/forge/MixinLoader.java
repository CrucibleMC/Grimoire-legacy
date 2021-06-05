package io.github.crucible.grimoire.mc1_7_10.mixins.forge;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModClassLoader;
import cpw.mods.fml.common.ModContainer;
import io.github.crucible.grimoire.common.core.GrimoireCore;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.mixin.transformer.Proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;

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
        // Add all mods to class loader

        for (ModContainer mod : this.mods) {
            try {
                this.modClassLoader.addFile(mod.getSource());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        GrimoireCore.INSTANCE.loadModMixins(); // Add all mod configurations

        // Force configurations to load once more
        Proxy mixinProxy = (Proxy) Launch.classLoader.getTransformers().stream().filter(transformer -> transformer instanceof Proxy).findFirst().get();
        try {
            Field transformerField = Proxy.class.getDeclaredField("transformer");
            transformerField.setAccessible(true);
            MixinTransformer transformer = (MixinTransformer) transformerField.get(mixinProxy);

            Method selectConfigsMethod = MixinTransformer.class.getDeclaredMethod("selectConfigs", MixinEnvironment.class);
            selectConfigsMethod.setAccessible(true);
            selectConfigsMethod.invoke(transformer, MixinEnvironment.getCurrentEnvironment());

            Method prepareConfigsMethod = MixinTransformer.class.getDeclaredMethod("prepareConfigs", MixinEnvironment.class);
            prepareConfigsMethod.setAccessible(true);
            prepareConfigsMethod.invoke(transformer, MixinEnvironment.getCurrentEnvironment());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        GrimoireCore.INSTANCE.finish(); // Dispatch final lifecycle events
    }
}
