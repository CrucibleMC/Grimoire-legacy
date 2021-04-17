package io.github.crucible.grimoire;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.crucible.grimoire.patch.GrimPatch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 5)
public class Grimoire implements IFMLLoadingPlugin {
    public static final Logger logger = LogManager.getLogger("grimoire");
    private static Grimoire instance;
    private final LaunchClassLoader classLoader = (LaunchClassLoader) Grimoire.class.getClassLoader();
    private final boolean shouldLog = !Boolean.parseBoolean(System.getProperty("grimoire.shutup", "false"));
    private final List<GrimPatch> grimPatchList = new ArrayList<>();

    public Grimoire() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins/mixins.grimoire.json");
        instance = this;
    }

    public static Grimoire getInstance() {
        return instance;
    }

    private void loadCoreMixins() {
        grimPatchList.forEach(grimPatch -> {
            if (grimPatch.isCorePatch()) {
                grimPatch.getMixinEntries().forEach(json -> Mixins.addConfiguration(json.getName())); //CorePatchs should be applied alongside Grimoire itself!
                if (shouldLog)
                    logger.info("[Grimoire] Applying Core-GrimPatch " + grimPatch.getPatchName());
            }
        });
    }

    public void loadAllMixins() {
        if (shouldLog) {
            logger.warn(" ");
            logger.warn("[Grimoire] Trying to apply mod mixins.");
            logger.warn("    - If you got any errors contact the developer immediately.");
            logger.warn(" ");
            logger.warn("Loaded files ({}):", grimPatchList.size());

            for (GrimPatch grimPatch : grimPatchList) {
                logger.warn(" - {}{}", grimPatch.getPatchName(), (grimPatch.isCorePatch() ? "(CorePatch! AlreadyLoaded)" : ""));
            }
            logger.warn(" ");
        }

        grimPatchList.forEach(grimPatch -> {
            if (!grimPatch.isCorePatch()) {
                grimPatch.getMixinEntries().forEach(json -> Mixins.addConfiguration(json.getName()));
            }
        });
    }

    private List<GrimPatch> scanForPatches(File dir, boolean legacy) {
        List<GrimPatch> grimPatches = new ArrayList<>();

        for (File mod : FileUtils.listFiles(dir, new String[]{"jar"}, true)) {
            if (!mod.canRead())
                continue;

            try (ZipFile zipFile = new ZipFile(mod)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                //GrimPatch Data
                List<ZipEntry> mixinEntries = new ArrayList<>();
                Manifest manifest = null;

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (isMixinConfiguration(entry.getName(), legacy)) {
                        mixinEntries.add(entry);
                    } else if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                        manifest = new Manifest(zipFile.getInputStream(entry));
                    }
                }

                if (!mixinEntries.isEmpty()) {
                    classLoader.addURL(mod.toPath().toUri().toURL());
                    grimPatches.add(new GrimPatch(manifest, mixinEntries, mod));
                }
            } catch (Exception e) {
                logger.error("[Grimoire] Unable to load '{}'.", mod.getAbsolutePath());
                e.printStackTrace();
            }
        }

        return grimPatches;
    }

    private File extractResources(Collection<String> resources) throws IOException {
        File tmp = Files.createTempDirectory("grimoire").toFile();
        for (String resource : resources) {
            try (InputStream resourceStream = getClass().getResourceAsStream('/'+resource)) {
                if (resourceStream == null) {
                    logger.warn("[Grimoire] Failed to copy embedded patch with name {}", resource);
                    continue;
                }
                File extracted = new File(tmp, resource.replace('/','_'));

                if (shouldLog)
                    logger.info("[Grimoire] Extracting embedded patch {} to {}", resource, extracted.getAbsolutePath());
                Files.copy(resourceStream, extracted.toPath());
            }
        }
        return tmp;
    }

    private boolean isMixinConfiguration(String name, boolean legacy) {
        String splitName = ((name.contains("/")) ?
                name.substring(name.lastIndexOf("/")).replace("/", "") :
                name); // Caso o zip esteja usando o "\" esse zip está quebrado, já que o padrão é o "/".

        if (legacy) {
            if (splitName.startsWith("mixins.") && splitName.endsWith(".json")) return true;
            if (splitName.startsWith("mixins-") && splitName.endsWith(".json")) return true;
            return splitName.endsWith("-mixins.json");
        }

        if (splitName.startsWith("mixins.grim.") && splitName.endsWith(".json")) return true;
        if (splitName.startsWith("mixins-grim-") && splitName.endsWith(".json")) return true;
        return splitName.endsWith("-grim-mixins.json");
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
    public void injectData(Map<String, Object> data) {
        //Search for grim patches on grimoire legacy folder
        File grimoireFolder = new File((File) data.get("mcLocation"), "grimoire");
        if (grimoireFolder.exists()) {
            grimPatchList.addAll(scanForPatches(grimoireFolder, true));
        }

        //Search for grim patches on mods folder
        grimPatchList.addAll(scanForPatches(new File((File) data.get("mcLocation"), "mods"), false));

        //Load for any patch inside the grimoire resource folder.
        try {
            grimPatchList.addAll(scanForPatches(extractResources(ResourceList.getResources(Pattern.compile(".*grimoire/.*\\.jar"), classLoader)), false));
        } catch (IOException e) {
            logger.error("Error while trying to load embedded patches.");
            e.printStackTrace();
        }

        Collections.sort(grimPatchList);
        Collections.reverse(grimPatchList);
        loadCoreMixins();
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    public List<GrimPatch> getGrimPatchList() {
        return grimPatchList;
    }
}
