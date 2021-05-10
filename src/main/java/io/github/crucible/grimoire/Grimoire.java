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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarFile;
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

    private static final String DEVENV_VAR = "fml.isDevEnvironment";
    private static final String GRIMPATCH_VAR = "fml.grimPatches.load";

    public Grimoire() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins/mixins.grimoire.json");
        instance = this;
    }

    public static Grimoire getInstance() {
        return instance;
    }

    private void loadCoreMixins() {
        this.grimPatchList.forEach(grimPatch -> {
            if (grimPatch.isCorePatch()) {
                // Core Patches should be applied alongside Grimoire itself!

                if (grimPatch.getMixinZipEntries() != null) {
                    grimPatch.getMixinZipEntries().forEach(json -> Mixins.addConfiguration(json.getName()));
                } else if (grimPatch.getMixinFileEntries() != null) {
                    grimPatch.getMixinFileEntries().forEach(file -> Mixins.addConfiguration(file.getName()));
                }

                if (this.shouldLog) {
                    logger.info("[Grimoire] Applying Core-GrimPatch " + grimPatch.getPatchName());
                }
            }
        });
    }

    public void loadAllMixins() {
        if (this.shouldLog) {
            logger.warn(" ");
            logger.warn("[Grimoire] Trying to apply mod mixins.");
            logger.warn("    - If you got any errors contact the developer immediately.");
            logger.warn(" ");
            logger.warn("Loaded files ({}):", this.grimPatchList.size());

            for (GrimPatch grimPatch : this.grimPatchList) {
                logger.warn(" - {}{}", grimPatch.getPatchName(), (grimPatch.isCorePatch() ? "(CorePatch! AlreadyLoaded)" : ""));
            }
            logger.warn(" ");
        }

        this.grimPatchList.forEach(grimPatch -> {
            if (!grimPatch.isCorePatch()) {
                if (grimPatch.getMixinZipEntries() != null) {
                    grimPatch.getMixinZipEntries().forEach(json -> Mixins.addConfiguration(json.getName()));
                } else if (grimPatch.getMixinFileEntries() != null) {
                    grimPatch.getMixinFileEntries().forEach(file -> Mixins.addConfiguration(file.getName()));
                }
            }
        });
    }

    private List<GrimPatch> scanForPatches(File dir, boolean legacy) {
        List<GrimPatch> grimPatches = new ArrayList<>();

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
                    if (this.isMixinConfiguration(entry.getName(), legacy, true)) {
                        mixinEntries.add(entry);
                    } else if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                        manifest = new Manifest(zipFile.getInputStream(entry));
                    }
                }

                if (!mixinEntries.isEmpty()) {
                    this.classLoader.addURL(mod.toPath().toUri().toURL());
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
            try (InputStream resourceStream = this.getClass().getResourceAsStream('/'+resource)) {
                if (resourceStream == null) {
                    logger.warn("[Grimoire] Failed to copy embedded patch with name {}", resource);
                    continue;
                }
                File extracted = new File(tmp, resource.replace('/','_'));

                if (this.shouldLog) {
                    logger.info("[Grimoire] Extracting embedded patch {} to {}", resource, extracted.getAbsolutePath());
                }
                Files.copy(resourceStream, extracted.toPath());
            }
        }
        return tmp;
    }

    private boolean isMixinConfiguration(String name, boolean legacy, boolean fullpath) {
        String splitName = name;

        if (fullpath) {
            splitName = ((name.contains("/")) ?
                    name.substring(name.lastIndexOf("/")).replace("/", "") :
                        name); // Caso o zip esteja usando o "\" esse zip está quebrado, já que o padrão é o "/".
        }

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
        if (this.isDevEnvironment()) { // Just handle what's passed in system properties if we are in dev environment
            logger.info("It would appear we find ourselves in dev environment; scanning system properties for GrimPatch paths...");

            List<URL> paths = this.getGrimpaths();
            List<GrimPatch> grimPatches = new ArrayList<>();

            for (URL url : paths) {
                try {
                    File coreMod = new File(url.toURI().getPath());
                    Manifest manifest = null;

                    if (!coreMod.exists()) {
                        continue;
                    }

                    if (coreMod.isDirectory()) {
                        File manifestFile = new File(coreMod, "META-INF/MANIFEST.MF");
                        if (manifestFile.exists()) {
                            FileInputStream stream = new FileInputStream(manifestFile);
                            manifest = new Manifest(stream);
                            stream.close();
                        }

                        List<File> mixinEntries = new ArrayList<>();

                        // Try to detect directory grimpatch
                        for (File file : coreMod.listFiles()) {
                            if (file.isFile() && this.isMixinConfiguration(file.getName(), false, true)) {
                                if (this.isMixinConfiguration(file.getName(), false, false)) {
                                    mixinEntries.add(file);
                                }
                            }
                        }

                        if (!mixinEntries.isEmpty()) {
                            grimPatches.add(new GrimPatch(manifest, coreMod, mixinEntries));
                        }
                    } else if (coreMod.getName().endsWith("jar")) { // its a jar
                        JarFile jar = new JarFile(coreMod);
                        manifest = jar.getManifest();

                        Enumeration<? extends ZipEntry> entries = jar.entries();
                        List<ZipEntry> mixinEntries = new ArrayList<>();

                        // Try to detect jar-file grimpatch
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (this.isMixinConfiguration(entry.getName(), false, true)) {
                                mixinEntries.add(entry);
                            }
                        }

                        jar.close();

                        if (!mixinEntries.isEmpty()) {
                            grimPatches.add(new GrimPatch(manifest, mixinEntries, coreMod));
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            this.grimPatchList.addAll(grimPatches);

            logger.info("Grand total of " + this.grimPatchList.size() + " GrimPatches was located; the list goes as following:");
            for (GrimPatch patch : this.grimPatchList) {
                logger.info("GrimPatch name: " + patch.getFileOrDirectoryName() + ", associated paths:");
                if (patch.getMixinZipEntries() != null) {
                    for (ZipEntry entry: patch.getMixinZipEntries()) {
                        logger.info("Zip entry: " + entry.getName());
                    }
                } else if (patch.getMixinFileEntries() != null) {
                    for (File entry: patch.getMixinFileEntries()) {
                        logger.info("File entry: " + entry.getPath());
                    }
                }
            }

        } else { // If we are not in dev environment, do our normal searching business
            // Search for grim patches on grimoire legacy folder
            File grimoireFolder = new File((File) data.get("mcLocation"), "grimoire");
            if (grimoireFolder.exists()) {
                this.grimPatchList.addAll(this.scanForPatches(grimoireFolder, true));
            }

            // Search for grim patches on mods folder
            this.grimPatchList.addAll(this.scanForPatches(new File((File) data.get("mcLocation"), "mods"), false));

            // Load for any patch inside the grimoire resource folder.
            try {
                this.grimPatchList.addAll(this.scanForPatches(this.extractResources(ResourceList.getResources(Pattern.compile(".*grimoire/.*\\.jar"), this.classLoader)), false));
            } catch (IOException e) {
                logger.error("Error while trying to load embedded patches.");
                e.printStackTrace();
            }
        }

        Collections.sort(this.grimPatchList);
        Collections.reverse(this.grimPatchList);
        this.loadCoreMixins();
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    public List<GrimPatch> getGrimPatchList() {
        return this.grimPatchList;
    }

    public boolean isDevEnvironment() {
        return Boolean.valueOf(System.getProperty(DEVENV_VAR));
    }

    private List<URL> getGrimpaths() {
        List<URL> list = new ArrayList<>();
        String paths = System.getProperty(GRIMPATCH_VAR);

        if (paths != null) {
            String[] split = paths.split("<@>");

            for (String path : split) {
                try {
                    list.add(new URL(path));
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return list;
    }
}
