package io.github.crucible.grimoire.mc1_7_10.mixins.bukkit;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.crucible.grimoire.common.GrimoireCore;

import java.io.File;
import java.lang.reflect.Method;
/*
@Pseudo
@Mixin(targets = "org.bukkit.plugin.java.PluginClassLoader", remap = false)
public class IMixinPluginClassLoader {

    @Inject(method = "<init>", at = @At(value = "RETURN"), remap = false)
    public void injectToConstructor(JavaPluginLoader loader, ClassLoader parent, PluginDescriptionFile description, File dataFolder, File file, CallbackInfo ci) {
        if (parent != null && parent instanceof LaunchClassLoader) {
            try {
                Method methode = parent.getClass().getDeclaredMethod("addChild", ClassLoader.class);
                methode.invoke(parent, this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
 */