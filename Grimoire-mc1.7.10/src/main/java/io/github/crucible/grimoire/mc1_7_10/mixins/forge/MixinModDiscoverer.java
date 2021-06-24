package io.github.crucible.grimoire.mc1_7_10.mixins.forge;

import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = ModDiscoverer.class, remap = false)
public class MixinModDiscoverer {

    @Shadow(remap = false)
    private List<ModCandidate> candidates;

    @Redirect(method = "findModDirMods(Ljava/io/File;[Ljava/io/File;)V", at = @At(
            target = "Ljava/util/List;contains(Ljava/lang/Object;)Z",
            value = "INVOKE"
            ))
    private boolean redirectCoremodCheck(List<?> loadedCoremods, Object fileName) {
        for (IGrimmix patch : GrimmixLoader.INSTANCE.getAllContainers()) {
            if (patch.getGrimmixFile().getName().equals(fileName) && !patch.isGrimoireGrimmix())
                // Mods which contains a GrimPatch are loaded by forge's class path mod loading.
                // Prevent forge from loading it again.
                return true;
        }

        return loadedCoremods.contains(fileName);
    }

}
