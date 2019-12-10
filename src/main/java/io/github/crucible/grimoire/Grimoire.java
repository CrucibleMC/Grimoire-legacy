package io.github.crucible.grimoire;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class Grimoire implements IFMLLoadingPlugin {

    private LaunchClassLoader classLoader;
    private List<File> loadedPatches = new ArrayList<>();

    public static Grimoire instance;

    public Grimoire() {
        instance = this;
        System.setProperty("mixin.debug.export", "true");

        if (!(Grimoire.class.getClassLoader() instanceof LaunchClassLoader))
            throw new RuntimeException("Coremod plugin class was loaded by another classloader!");

        LogManager.getLogger().warn(" ");
        LogManager.getLogger().warn("[Grimoire] Applying ModCompat MixinLoader!");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins/mixins.grimoire.json");
        LogManager.getLogger().warn(" ");
    }

    public void loadAllMixins(){

        LogManager.getLogger().warn(" ");
        LogManager.getLogger().warn("[Grimoire] Trying to apply class transformers.");
        LogManager.getLogger().warn("    - If you got any errors contact the developer immediately.");
        LogManager.getLogger().warn(" ");

        classLoader = (LaunchClassLoader) Grimoire.class.getClassLoader();

        File dir = new File("grimoire");
        if (!dir.exists()) {
            LogManager.getLogger().warn("[Grimoire] Creating mixins folder.");
            if (!dir.mkdirs()) LogManager.getLogger().warn("[Grimoire] Unable to create the folder " + dir.getAbsolutePath());
        }

        if (dir.listFiles().length == 0){ //No Grimoire Mixin
            LogManager.getLogger().warn("[Grimoire] No grimoire-mixin found!");
            return;
        }

        LinkedList<File> allFiles = new LinkedList<>(FileUtils.listFiles(dir,new String[]{"jar"},true));
        //Collections.sort(allFiles,Comparator.comparing(File::getName));//Arealdy Sorted, but.... just in case :D
        //allFiles = sortedFiles(allFiles);

        //allFiles.removeIf(file -> {
        //    if (file.getName().startsWith("[")){
        //        tryToLoadPatch(file);//It is a Priority Patch, then, load it first!
        //        return true;
        //    }
        //    return false;
        //});

        //allFiles.forEach(this::tryToLoadPatch);//Load the Rest!
        sortedFiles(allFiles).forEach(this::tryToLoadPatch);

        LogManager.getLogger().warn(String.format("Loaded files %s","(" + loadedPatches.size() + "):"));
        for (File file : loadedPatches) {
            LogManager.getLogger().warn(" - " + file.getName());
        }
        LogManager.getLogger().warn(" ");

        Grimoire.instance = null; //Don't need the instance anymore :V
    }

    private void tryToLoadPatch(File mod){
        if (!mod.canRead()) return;
        try {
            ZipFile zipFile = new ZipFile(mod);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            List<ZipEntry> mixinEntries = new ArrayList<>();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (isMixinConfiguration(entry.getName())) {
                    mixinEntries.add(entry);
                }
            }

            if (!mixinEntries.isEmpty()) {
                classLoader.addURL(mod.toPath().toUri().toURL());
                mixinEntries.forEach(json -> Mixins.addConfiguration(json.getName()));
                loadedPatches.add(mod);
                return;
            }
        } catch (Exception e) {
            LogManager.getLogger().warn("[Grimoire] Unable to load \'" + mod.getAbsolutePath() + "\'.");
            e.printStackTrace();
        }
        return;
    }

    private boolean isMixinConfiguration(String name) {
        String splitName = ((name.contains("/")) ? name.substring(name.lastIndexOf("/")).replace("/", "") : name); // Caso o zip esteja usando o "\" esse zip está quebrado, já que o padrão é o "/".
        if (splitName.startsWith("mixins.") && splitName.endsWith(".json")) return true;
        if (splitName.startsWith("mixins-") && splitName.endsWith(".json")) return true;
        return splitName.endsWith("-mixins.json");
    }

    private LinkedList<File> sortedFiles(List<File> files){
        LinkedHashMap<File,Integer> sortedMods = new LinkedHashMap<>();

        for(File file : files){
            try {
                ZipFile zipFile = new ZipFile(file);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {

                    ZipEntry entry = entries.nextElement();

                    if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                        Manifest manifest = new Manifest(zipFile.getInputStream(entry));
                        int priority = 100;

                        if(manifest != null){

                            try{
                                priority = (manifest.getMainAttributes().getValue("GRIMOIRE_PRIORITY") != null) ? Integer.parseInt(manifest.getMainAttributes().getValue("GRIMOIRE_PRIORITY")) : 100;
                            }catch (Exception ignored){}

                            System.out.println("Encontrado a prioridade " + priority + " em " + zipFile.getName());

                        }

                        sortedMods.put(file, priority);

                        break;
                    }

                }

            } catch (IOException e) {
                sortedMods.put(file,100);
            }
        }

        LinkedList<Map.Entry<File,Integer>> sortedModsFinal = new LinkedList<>(sortedMods.entrySet());

        sortedModsFinal.sort(new Comparator<Map.Entry<File, Integer>>() {
            public int compare(Map.Entry<File, Integer> o1,
                               Map.Entry<File, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        LinkedList<File> sorted = new LinkedList<>();
        for(Map.Entry<File,Integer> entry : sortedModsFinal){
            sorted.add(entry.getKey());
        }

        System.out.println(sorted);
        return sorted;
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
