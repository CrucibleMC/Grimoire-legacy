package io.github.crucible.grimoire;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.crucible.grimoire.patch.GrimPatch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.*;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class Grimoire implements IFMLLoadingPlugin {

    private LaunchClassLoader classLoader;
    private List<GrimPatch> grimPatchList = new ArrayList<>();

    public static Grimoire instance;

    public Grimoire() {
        instance = this;
        System.setProperty("mixin.debug.export", "true");

        if (!(Grimoire.class.getClassLoader() instanceof LaunchClassLoader))
            throw new RuntimeException("Coremod plugin class was loaded by another classloader!");

        classLoader = (LaunchClassLoader) Grimoire.class.getClassLoader();

        LogManager.getLogger().warn(" ");
        LogManager.getLogger().warn("[Grimoire] Applying ModCompat MixinLoader!");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins/mixins.grimoire.json");
        LogManager.getLogger().warn(" ");

        File grimoireFolder = new File("grimoire");
        if (!grimoireFolder.exists()) {
            LogManager.getLogger().warn("[Grimoire] Creating mixins folder.");
            if (!grimoireFolder.mkdirs()) LogManager.getLogger().warn("[Grimoire] Unable to create the folder " + grimoireFolder.getAbsolutePath());
        }

        if (grimoireFolder.listFiles().length == 0){ //No Grimoire Mixin
            LogManager.getLogger().warn("[Grimoire] No grimoire-mixin found!");
            return;
        }

        grimPatchList = scanForPatches(grimoireFolder);
        Collections.sort(grimPatchList);
        Collections.reverse(grimPatchList);

        grimPatchList.forEach(grimPatch -> {
            if (grimPatch.isCorePatch()){
                grimPatch.getMixinEntries().forEach(json -> Mixins.addConfiguration(json.getName())); //CorePatchs should be applied alongside Grimoire itself!
                LogManager.getLogger().warn("[Grimoire] Applying Core-GrimPatch " + grimPatch.getPatchName());
            }
        });
    }

    public void loadAllMixins(){
        LogManager.getLogger().warn(" ");
        LogManager.getLogger().warn("[Grimoire] Trying to apply class transformers.");
        LogManager.getLogger().warn("    - If you got any errors contact the developer immediately.");
        LogManager.getLogger().warn(" ");

        grimPatchList.forEach(grimPatch -> {
            if (!grimPatch.isCorePatch()) {
                grimPatch.getMixinEntries().forEach(json -> Mixins.addConfiguration(json.getName()));
            }
        });

        LogManager.getLogger().warn(String.format("Loaded files %s","(" + grimPatchList.size() + "):"));

        for (GrimPatch grimPatch : grimPatchList) {
            LogManager.getLogger().warn(" - " + grimPatch.getPatchName() + (grimPatch.isCorePatch() ? "(CorePatch! AlreadyLoaded)" : ""));
        }
        LogManager.getLogger().warn(" ");

        Grimoire.instance = null; //Don't need the instance anymore :V
    }

    private List<GrimPatch> scanForPatches(File dir){
        List<GrimPatch> grimPatchList = new ArrayList<>();
        for (File mod : FileUtils.listFiles(dir, new String[]{"jar"}, true)) {
            if (!mod.canRead()) continue;
            try {
                ZipFile zipFile = new ZipFile(mod);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                //GrimPatch Data
                List<ZipEntry> mixinEntries = new ArrayList<>();
                Manifest manifest = null;

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (isMixinConfiguration(entry.getName())) {
                        mixinEntries.add(entry);
                    }else if (isManifest(entry.getName())) {
                        manifest = new Manifest(zipFile.getInputStream(entry));
                    }
                }

                if (!mixinEntries.isEmpty()) {
                    classLoader.addURL(mod.toPath().toUri().toURL());
                    grimPatchList.add(new GrimPatch(manifest,mixinEntries, mod));
                }
            } catch (Exception e) {
                LogManager.getLogger().warn("[Grimoire] Unable to load \'" + mod.getAbsolutePath() + "\'.");
                e.printStackTrace();
            }
        }
        return grimPatchList;
    }

    private boolean isMixinConfiguration(String name) {
        String splitName = ((name.contains("/")) ? name.substring(name.lastIndexOf("/")).replace("/", "") : name); // Caso o zip esteja usando o "\" esse zip está quebrado, já que o padrão é o "/".
        if (splitName.startsWith("mixins.") && splitName.endsWith(".json")) return true;
        if (splitName.startsWith("mixins-") && splitName.endsWith(".json")) return true;
        return splitName.endsWith("-mixins.json");
    }

    private boolean isManifest(String name) {
        return name.equals("META-INF/MANIFEST.MF");
    }

    private void applyGrimPatch(GrimPatch grimPatch){
        if (!grimPatch.getModId().isEmpty()){

        }
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
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
