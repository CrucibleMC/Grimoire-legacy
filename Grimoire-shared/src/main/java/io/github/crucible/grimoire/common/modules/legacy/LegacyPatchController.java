package io.github.crucible.grimoire.common.modules.legacy;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;
import io.github.crucible.grimoire.common.config.GrimoireConfig;
import io.github.crucible.grimoire.common.core.GrimmixLoader;

@Grimmix(id = "GrimoireLegacyModule", name = "Grimoire Legacy Module")
public class LegacyPatchController extends GrimmixController {
    private static LegacyPatchController instance;
    private static List<LegacyPatch> patches = new ArrayList<>();
    private final IGrimmix grimmix;

    public LegacyPatchController() {
        super();

        instance = this;
        this.grimmix = GrimmixLoader.INSTANCE.getActiveContainer();
    }

    public static LegacyPatchController instance() {
        return instance;
    }

    public boolean isEnabled() {
        return this.grimmix != null && this.grimmix.isValid();
    }

    public boolean isLegacyPatch(String fileName) {
        for (LegacyPatch patch : patches) {
            if (patch.getFileName().equals(fileName) && !patch.wasOnClasspath())
                return true;
        }

        return false;
    }

    @Override
    public void validateController(IValidationEvent event) {
        if (!GrimoireConfig.enableLegacySupport) {
            event.invalidate();
        } else {
            GrimoireCore.logger.info("Legacy support module is enabled. Be looking for legacy patch files...");

            File grimoireFolder = new File(GrimoireAPI.getMinecraftFolder(), "grimoire");
            File modFolder = GrimoireAPI.getModFolder();
            File versionFolder = GrimoireAPI.getVersionedModFolder();

            if (grimoireFolder.exists() && grimoireFolder.isDirectory()) {
                patches.addAll(this.scanForPatches(grimoireFolder, true));
            }

            if (modFolder.exists() && modFolder.isDirectory()) {
                patches.addAll(this.scanForPatches(modFolder, false));
            }

            if (versionFolder.exists() && versionFolder.isDirectory()) {
                patches.addAll(this.scanForPatches(versionFolder, false));
            }

            Collections.sort(patches);
            Collections.reverse(patches);

            GrimoireCore.logger.info("Grand total of {} legacy patches was located.", patches.size());
        }
    }

    @Override
    public void coreLoad(ICoreLoadEvent event) {
        patches.forEach(grimPatch -> {
            if (grimPatch.isCorePatch()) {
                GrimoireCore.logger.info("Applying core legacy patch " + grimPatch.getPatchName());
                grimPatch.getMixinEntries().forEach(json -> event.registerConfiguration(json.getName()));
            }
        });
    }

    @Override
    public void modLoad(IModLoadEvent event) {
        patches.forEach(grimPatch -> {
            if (!grimPatch.isCorePatch()) {
                GrimoireCore.logger.info("Applying mod legacy patch " + grimPatch.getPatchName());
                grimPatch.getMixinEntries().forEach(json -> event.registerConfiguration(json.getName()));
            }
        });
    }

    private boolean isMixinConfiguration(String name, boolean legacy) {
        String splitName = ((name.contains("/")) ?
                name.substring(name.lastIndexOf("/")).replace("/", "") :
                    name);

        if (legacy) {
            if (splitName.startsWith("mixins.") && splitName.endsWith(".json")) return true;
            if (splitName.startsWith("mixins-") && splitName.endsWith(".json")) return true;
            return splitName.endsWith("-mixins.json");
        }

        if (splitName.startsWith("mixins.grim.") && splitName.endsWith(".json")) return true;
        if (splitName.startsWith("mixins-grim-") && splitName.endsWith(".json")) return true;
        return splitName.endsWith("-grim-mixins.json");
    }

    private List<LegacyPatch> scanForPatches(File dir, boolean legacy) {
        List<LegacyPatch> grimPatches = new ArrayList<>();

        for (File mod : FileUtils.listFiles(dir, new String[]{"jar"}, true)) {
            if (!mod.canRead()) {
                continue;
            }

            try (ZipFile zipFile = new ZipFile(mod)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                //GrimPatch Data
                List<ZipEntry> mixinEntries = new ArrayList<>();
                Manifest manifest = null;

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (this.isMixinConfiguration(entry.getName(), legacy)) {
                        mixinEntries.add(entry);
                    } else if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                        manifest = new Manifest(zipFile.getInputStream(entry));
                    }
                }

                if (!mixinEntries.isEmpty()) {
                    boolean hasGrimmix = false;

                    for (IGrimmix grimmix : GrimmixLoader.INSTANCE.getAllContainers()) {
                        if (grimmix.getGrimmixFile().equals(mod)) {
                            hasGrimmix = true;
                            break;
                        }
                    }

                    // Ignore files that have proper controller, otherwise we have high possibility
                    // of accidentally force-loading configurations nobody asked us to force-load.
                    if (hasGrimmix) {
                        GrimoireCore.logger.info("Ignoring legacy patch candidate {}, as it contains modern @Grimmix.", mod.getName());
                        continue;
                    }

                    URL modUrl = mod.toPath().toUri().toURL();
                    boolean alreadyThere = false;

                    for (URL url : GrimoireCore.INSTANCE.getClassLoader().getURLs()) {
                        if (modUrl.equals(url)) {
                            alreadyThere = true;
                            break;
                        }
                    }

                    if (!alreadyThere) {
                        GrimoireCore.INSTANCE.getClassLoader().addURL(modUrl);
                        GrimoireCore.INSTANCE.getForcedFilenames().add(mod.getName());
                    }

                    GrimoireCore.logger.info("Added legacy patch {}, was on classpath: {}", mod.getName(), alreadyThere);

                    grimPatches.add(new LegacyPatch(manifest, mixinEntries, mod, alreadyThere));
                }
            } catch (Exception ex) {
                GrimoireCore.logger.error("Unable to load '{}'.", mod.getAbsolutePath());
                ex.printStackTrace();
            }
        }

        return grimPatches;
    }

}
