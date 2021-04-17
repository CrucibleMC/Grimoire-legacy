package io.github.crucible.grimoire.mixins.forge;

import cpw.mods.fml.common.discovery.ModDiscoverer;
import io.github.crucible.grimoire.Grimoire;
import io.github.crucible.grimoire.patch.GrimPatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = ModDiscoverer.class, remap = false)
public class MixinModDiscoverer {

    @Redirect(
            method = "findModDirMods(Ljava/io/File;[Ljava/io/File;)V",
            at = @At(
                    target = "Ljava/util/List;contains(Ljava/lang/Object;)Z",
                    value = "INVOKE"
            ))
    private boolean redirectCoremodCheck(List loadedCoremods, Object fileName) {
        for (GrimPatch patch : Grimoire.getInstance().getGrimPatchList()) {
            if (patch.getFileName().equals(fileName))
                // Mods which contains a GrimPatch are loaded by forge's class path mod loading.
                // Prevent forge from loading it again.
                return true;
        }
        return loadedCoremods.contains(fileName);
    }
}
