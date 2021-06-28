
package io.github.crucible.grimoire.mc1_12_2.mixins.forge;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.transformer.Proxy;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.grimmix.Grimmix;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;

@Mixin(value = Loader.class, remap = false)
public class MixinLoader {

    @Shadow(remap = false)
    private List<ModContainer> mods;

    @Shadow(remap = false)
    private ModClassLoader modClassLoader;

    // TODO Test if below redirect actually properly works in production

    /**
     * @reason Mod discovery process will add duplicate instances of files
     * that embed normal {@link Mod} along with {@link Grimmix}. Prevent this
     * by making discovery ignore files which were already added to classpath
     * by Grimoire; these will be handled separately anyways.
     */
    @Redirect(method = "identifyMods(Ljava/util/List;)Lnet/minecraftforge/fml/common/discovery/ModDiscoverer;",
            at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z", ordinal = 1),
            remap = false)
    private boolean onModIdentification(List<?> loadedCoremods, Object file)  {
        String fileName = String.valueOf(file);

        if (GrimoireCore.INSTANCE.getForcedFilenames().contains(fileName)) {
            GrimoireCore.logger.info("File {} was discovered by Grimoire and added to classpath earlier, preventing re-addition by ModDiscovered.", fileName);
            return true;
        }

        return loadedCoremods.contains(fileName);
    }

    /**
     * @reason Load all mods now and load mod support mixin configs. This can't be done later
     * since constructing mods loads classes from them.
     */
    @Inject(method = "loadMods", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/LoadController;transition(Lnet/minecraftforge/fml/common/LoaderState;Z)V", ordinal = 1), remap = false)
    private void beforeConstructingMods(CallbackInfo ci) {

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