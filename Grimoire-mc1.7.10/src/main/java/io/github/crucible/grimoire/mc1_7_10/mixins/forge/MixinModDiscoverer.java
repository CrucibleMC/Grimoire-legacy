package io.github.crucible.grimoire.mc1_7_10.mixins.forge;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.modules.legacy.LegacyPatchController;

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
    private boolean redirectCoremodCheck(List<?> loadedCoremods, Object file) {
        String fileName = String.valueOf(file);

        if (GrimoireCore.INSTANCE.getForcedFilenames().contains(fileName)) {
            GrimoireCore.logger.info("File {} was discovered by Grimoire and added to classpath earlier, preventing re-addition by ModDiscovered.", fileName);
            return true;
        }

        return loadedCoremods.contains(fileName);
    }

}
