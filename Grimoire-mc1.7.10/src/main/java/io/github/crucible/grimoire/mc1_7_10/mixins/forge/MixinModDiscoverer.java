package io.github.crucible.grimoire.mc1_7_10.mixins.forge;

import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.core.GrimmixContainer;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.modules.legacy.LegacyPatchController;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = ModDiscoverer.class, remap = false)
public class MixinModDiscoverer {

    @Shadow(remap = false)
    private List<ModCandidate> candidates;

    /**
     * Mods which contains a GrimPatch are loaded by forge's class path mod loading.
     * Prevent forge from loading it again, but only if Grimoire was the one to add
     * them onto classpath. If they were already there, they probably have their own
     * means of preventing double-loading.
     */

    @Redirect(method = "findModDirMods(Ljava/io/File;[Ljava/io/File;)V", at = @At(
            target = "Ljava/util/List;contains(Ljava/lang/Object;)Z",
            value = "INVOKE"
            ))
    private boolean redirectCoremodCheck(List<?> loadedCoremods, Object fileName) {
        if (GrimmixLoader.INSTANCE.isGrimmix(String.valueOf(fileName)))
            return true;
        else if (LegacyPatchController.instance().isLegacyPatch(String.valueOf(fileName)))
            return true;


        return loadedCoremods.contains(fileName);
    }

}
